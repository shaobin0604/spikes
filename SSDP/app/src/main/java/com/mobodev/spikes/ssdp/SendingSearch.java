package com.mobodev.spikes.ssdp;

import android.util.Log;

public class SendingSearch extends SendingAsync {
    private static final String TAG = "SendingSearch";

    private final int mxSeconds;

    /**
     * @param mxSeconds The time in seconds a host should wait before responding.
     */
    public SendingSearch(int mxSeconds) {
        this.mxSeconds = mxSeconds;
    }

    public int getMxSeconds() {
        return mxSeconds;
    }

    protected void execute() throws RouterException {

        Log.v(TAG, "Executing search for device with MX seconds: " + getMxSeconds());

        for (int i = 0; i < getBulkRepeat(); i++) {
            try {

                final OutgoingDatagramMessage<RequestMessage> msg = new OutgoingDatagramMessage<>(
                        ModelUtil.getInetAddressByName(Constants.IPV4_UPNP_MULTICAST_GROUP),
                        Constants.UPNP_MULTICAST_PORT,
                        new RequestMessage(i + 1, "search"));

                prepareOutgoingSearchRequest(msg);

                Globals.getInstance().getRouter().send(msg);

                // UDA 1.0 is silent about this but UDA 1.1 recommends "a few hundred milliseconds"
                Log.v(TAG, "Sleeping " + getBulkIntervalMilliseconds() + " milliseconds");
                Thread.sleep(getBulkIntervalMilliseconds());

            } catch (InterruptedException ex) {
                // Interruption means we stop sending search messages, e.g. on shutdown of thread pool
                break;
            }
        }
    }

    public int getBulkRepeat() {
        return 5; // UDA 1.0 says "repeat more than once"
    }

    public int getBulkIntervalMilliseconds() {
        return 500; // That should be plenty on an ethernet LAN
    }

    /**
     * Override this to edit the outgoing message, e.g. by adding headers.
     */
    protected void prepareOutgoingSearchRequest(OutgoingDatagramMessage<RequestMessage> message) {
    }
}
