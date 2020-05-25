package com.mobodev.spikes.ssdp;


import android.util.Log;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Router {
    private static final String TAG = "Router";
    protected ReentrantReadWriteLock routerLock = new ReentrantReadWriteLock(true);
    protected Lock readLock = routerLock.readLock();
    protected Lock writeLock = routerLock.writeLock();
    protected MulticastReceiver multicastReceiver;
    protected DatagramIO datagramIO;
    private volatile boolean enabled;

    private boolean isDevice;

    public Router(boolean isDevice) {
        this.isDevice = isDevice;
    }

    public boolean enable() throws RouterException {
        lock(writeLock);
        try {
            if (!enabled) {
                try {
                    Log.v(TAG, "Starting networking services...");

                    if (isDevice) {
                        multicastReceiver = new MulticastReceiver(this, Globals.getInstance().getDatagramProcessor());
                        Log.v(TAG, "Starting multicast receiver");
                        Globals.getInstance().getDefaultExecutorService().execute(multicastReceiver);
                    }

                    datagramIO = new DatagramIO(this, Globals.getInstance().getDatagramProcessor());
                    Log.v(TAG, "Starting datagram I/O");
                    Globals.getInstance().getDefaultExecutorService().execute(datagramIO);

                    enabled = true;
                    return true;
                } catch (InitializationException ex) {
                    handleStartFailure(ex);
                }
            }
            return false;
        } finally {
            unlock(writeLock);
        }
    }

    public boolean disable() throws RouterException {
        lock(writeLock);
        try {
            if (enabled) {
                Log.v(TAG, "Disabling network services...");
                multicastReceiver.stop();
                datagramIO.stop();
                enabled = false;
                return true;
            }
            return false;
        } finally {
            unlock(writeLock);
        }
    }

    public void shutdown() throws RouterException {
        disable();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void handleStartFailure(InitializationException ex) throws InitializationException {
        Log.e(TAG, "Unable to initialize network router: " + ex);
        Log.e(TAG, "Cause: " + Exceptions.unwrap(ex));
    }

    public void send(OutgoingDatagramMessage msg) throws RouterException {
        lock(readLock);
        try {
            if (enabled) {
                datagramIO.send(msg);
            } else {
                Log.v(TAG, "Router disabled, not sending datagram: " + msg);
            }
        } finally {
            unlock(readLock);
        }
    }

    /**
     * <p>
     * This method is called internally by the transport layer when a datagram, either unicast or multicast, has been
     * received. An implementation of this interface has to handle the received message, e.g. selecting and executing a
     * UPnP protocol. This method should not block until the execution completes, the calling thread should be free to
     * handle the next reception as soon as possible.
     * </p>
     *
     * @param msg The received datagram message.
     */
    public void received(IncomingDatagramMessage msg) {
        if (!enabled) {
            Log.w(TAG, "Router disabled, ignoring incoming message: " + msg);
            return;
        }
        try {
            ReceivingAsync protocol = createReceivingAsync(msg);
            if (protocol == null) {
                Log.w(TAG, "No protocol, ignoring received message: " + msg);
                return;
            }

            Log.v(TAG, "Received asynchronous message: " + msg);
            Globals.getInstance().getDefaultExecutorService().execute(protocol);
        } catch (ProtocolCreationException ex) {
            Log.w(TAG, "Handling received datagram failed - " + Exceptions.unwrap(ex).toString());
        }
    }

    private ReceivingAsync createReceivingAsync(IncomingDatagramMessage message) throws ProtocolCreationException {
        Log.v(TAG, "Creating protocol for incoming asynchronous: " + message);

        final Message message1 = message.getMessage();

        if (message1 instanceof RequestMessage) {
            return new ReceivingSearch(message);
        } else if (message1 instanceof ResponseMessage) {
            return new ReceivingSearchResponse(message);
        }

        throw new ProtocolCreationException("Protocol for incoming datagram message not found: " + message);
    }

    protected void lock(Lock lock, int timeoutMilliseconds) throws RouterException {
        try {
            Log.v(TAG, "Trying to obtain lock with timeout milliseconds '" + timeoutMilliseconds + "': "
                    + lock.getClass().getSimpleName());
            if (lock.tryLock(timeoutMilliseconds, TimeUnit.MILLISECONDS)) {
                Log.v(TAG, "Acquired router lock: " + lock.getClass().getSimpleName());
            } else {
                throw new RouterException(
                        "Router wasn't available exclusively after waiting " + timeoutMilliseconds + "ms, lock failed: "
                                + lock.getClass().getSimpleName()
                );
            }
        } catch (InterruptedException ex) {
            throw new RouterException(
                    "Interruption while waiting for exclusive access: " + lock.getClass().getSimpleName(), ex
            );
        }
    }

    protected void lock(Lock lock) throws RouterException {
        lock(lock, getLockTimeoutMillis());
    }

    protected void unlock(Lock lock) {
        Log.v(TAG, "Releasing router lock: " + lock.getClass().getSimpleName());
        lock.unlock();
    }

    /**
     * @return Defaults to 6 seconds, should be longer than it takes the router to be enabled/disabled.
     */
    protected int getLockTimeoutMillis() {
        return 6000;
    }

}
