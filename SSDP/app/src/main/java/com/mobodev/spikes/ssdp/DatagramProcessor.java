package com.mobodev.spikes.ssdp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.util.Arrays;

public class DatagramProcessor {
    private static final String TAG = "DatagramProcessor";

    public IncomingDatagramMessage read(DatagramPacket datagram) throws UnsupportedDataException {
        final byte[] data = datagram.getData();
        try {
            String string = new String(data, "utf-8");
            JSONObject jsonObject = new JSONObject(string);
            if (jsonObject.has("operation")) {
                return new IncomingDatagramMessage<>(datagram.getAddress(), datagram.getPort(), new RequestMessage(jsonObject));
            } else if (jsonObject.has("status")) {
                return new IncomingDatagramMessage<>(datagram.getAddress(), datagram.getPort(), new ResponseMessage(jsonObject));
            } else {
                throw new UnsupportedDataException("incoming string invalid: " + string);
            }
        } catch (UnsupportedEncodingException | JSONException e) {
            throw new UnsupportedDataException("incoming data invalid: " + Arrays.toString(data));
        }
    }

    public DatagramPacket write(OutgoingDatagramMessage message) throws UnsupportedDataException {
        final Message message1 = message.getMessage();

        final byte[] data = message1.toBytes();

        Log.v(TAG, "Writing new datagram packet with " + data.length + " bytes for: " + message1);

        return new DatagramPacket(data, data.length, message.getDestinationAddress(), message.getDestinationPort());
    }
}
