package com.mobodev.spikes.ssdp;

import android.os.SystemClock;
import android.util.Log;

public class ReceivingSearchResponse extends ReceivingAsync<ResponseMessage> {
    private static final String TAG = "ReceivingSearchResponse";
    public ReceivingSearchResponse(
            IncomingDatagramMessage<ResponseMessage> inputMessage) {
        super(inputMessage);
    }

    @Override
    protected void execute() throws RouterException {
        final IncomingDatagramMessage<ResponseMessage> inputMessage = getInputMessage();

        Log.v(TAG, "Received device search response: " + inputMessage);

        final SampleData sampleData = Globals.getInstance().getSampleData();
        sampleData.putReceiveTs((int) inputMessage.getMessage().id, SystemClock.elapsedRealtime());
    }
}
