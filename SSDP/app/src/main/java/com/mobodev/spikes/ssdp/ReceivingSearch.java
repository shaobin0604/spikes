package com.mobodev.spikes.ssdp;

import android.util.Log;

import java.util.Random;

public class ReceivingSearch extends ReceivingAsync<RequestMessage> {
    private static final String TAG = "ReceivingSearch";

    // Spec says we should assume "less" if it's 120 or more
    // From the spec, MX should be "greater than or equal to 1"
    // Prevent negative MX to make nextInt() throw IllegalArgumentException below
    private static final int DEFAULT_MX_VALUE = 3;

    final protected Random randomGenerator = new Random();

    public ReceivingSearch(
            IncomingDatagramMessage<RequestMessage> inputMessage) {
        super(inputMessage);
    }

    @Override
    protected void execute() throws RouterException {
        sendResponse();
    }

    @Override
    protected boolean waitBeforeExecution() throws InterruptedException {
        int sleepTime = randomGenerator.nextInt(3 * 1000);
        Log.i(TAG,
                "waitBeforeExecution, Sleeping " + sleepTime + " milliseconds to avoid flooding with search responses");
        Thread.sleep(sleepTime);
        return true;
    }

    private void sendResponse() throws RouterException {
        final IncomingDatagramMessage<RequestMessage> inputMessage = getInputMessage();
        final RequestMessage requestMessage = inputMessage.getMessage();

        OutgoingDatagramMessage<ResponseMessage> message = new OutgoingDatagramMessage<>(
                inputMessage.getSourceAddress(), inputMessage.getSourcePort(),
                new ResponseMessage(requestMessage.getId(), "OK"));

        Globals.getInstance().getRouter().send(message);
    }
}
