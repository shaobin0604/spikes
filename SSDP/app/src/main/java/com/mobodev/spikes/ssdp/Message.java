package com.mobodev.spikes.ssdp;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public abstract class Message {
    public long id;

    public Message() {
    }

    public Message(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public abstract JSONObject toJSONObject();

    public byte[] toBytes() {
        try {
            return toJSONObject().toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
