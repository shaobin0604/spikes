package com.mobodev.spikes.ssdp;

import android.util.Log;

public abstract class ReceivingAsync<M extends Message> implements Runnable {

    private static final String TAG = "ReceivingAsync";

    private IncomingDatagramMessage<M> inputMessage;

    public ReceivingAsync(IncomingDatagramMessage<M> inputMessage) {
        this.inputMessage = inputMessage;
    }

    public IncomingDatagramMessage<M> getInputMessage() {
        return inputMessage;
    }

    public void run() {
        boolean proceed;
        try {
            proceed = waitBeforeExecution();
        } catch (InterruptedException ex) {
            Log.w(TAG, "Protocol wait before execution interrupted (on shutdown?): " + getClass().getSimpleName());
            proceed = false;
        }

        if (proceed) {
            try {
                execute();
            } catch (Exception ex) {
                Throwable cause = Exceptions.unwrap(ex);
                if (cause instanceof InterruptedException) {
                    Log.i(TAG, "Interrupted protocol '" + getClass().getSimpleName() + "': " + ex, cause);
                } else {
                    throw new RuntimeException(
                            "Fatal error while executing protocol '" + getClass().getSimpleName() + "': " + ex, ex
                    );
                }
            }
        }
    }

    /**
     * Provides an opportunity to pause before executing the protocol.
     *
     * @return <code>true</code> (default) if execution should continue after waiting.
     * @throws InterruptedException If waiting has been interrupted, which also stops execution.
     */
    protected boolean waitBeforeExecution() throws InterruptedException {
        // Don't wait by default
        return true;
    }

    protected abstract void execute() throws RouterException;

}
