package com.mobodev.spikes.ssdp;

import android.util.Log;

public abstract class SendingAsync implements Runnable {
    private static final String TAG = "SendingAsync";
    public void run() {
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

    protected abstract void execute() throws RouterException;

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }

}
