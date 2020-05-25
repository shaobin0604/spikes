package com.mobodev.spikes.ssdp;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseMessage extends Message {
    protected String status;

    public ResponseMessage(long id, String status) {
        super(id);
        this.status = status;
    }

    public ResponseMessage(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getLong("id");
            this.status = jsonObject.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @NonNull
    @Override
    public String toString() {
        return "ResponseMessage{" +
                "status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
