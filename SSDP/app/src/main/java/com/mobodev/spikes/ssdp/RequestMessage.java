package com.mobodev.spikes.ssdp;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestMessage extends Message {
    protected String operation;

    public RequestMessage(long id, String operation) {
        super(id);
        this.operation = operation;
    }

    public RequestMessage(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getLong("id");
            this.operation = jsonObject.getString("operation");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("operation", operation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
