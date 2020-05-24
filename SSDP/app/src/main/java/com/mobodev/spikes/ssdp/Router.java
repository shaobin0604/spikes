package com.mobodev.spikes.ssdp;


import android.util.Log;

import java.util.logging.Level;

public class Router {
    private static final String TAG = "Router";

    private boolean enabled;


    /**
     * <p>
     * This method is called internally by the transport layer when a datagram, either unicast or
     * multicast, has been received. An implementation of this interface has to handle the received
     * message, e.g. selecting and executing a UPnP protocol. This method should not block until
     * the execution completes, the calling thread should be free to handle the next reception as
     * soon as possible.
     * </p>
     * @param msg The received datagram message.
     */
    public void received(IncomingDatagramMessage msg) {
        if (!enabled) {
            Log.w(TAG, "Router disabled, ignoring incoming message: " + msg);
            return;
        }
        try {
            ReceivingAsync protocol = getProtocolFactory().createReceivingAsync(msg);
            if (protocol == null) {
                if (log.isLoggable(Level.FINEST))
                    log.finest("No protocol, ignoring received message: " + msg);
                return;
            }
            if (log.isLoggable(Level.FINE))
                log.fine("Received asynchronous message: " + msg);
            getConfiguration().getAsyncProtocolExecutor().execute(protocol);
        } catch (ProtocolCreationException ex) {
            log.warning("Handling received datagram failed - " + Exceptions.unwrap(ex).toString());
        }
    }

}
