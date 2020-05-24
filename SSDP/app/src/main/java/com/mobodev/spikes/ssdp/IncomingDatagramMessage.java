package com.mobodev.spikes.ssdp;

import androidx.annotation.NonNull;

import java.net.InetAddress;

public class IncomingDatagramMessage<M extends Message>  {
    private InetAddress sourceAddress;
    private int sourcePort;
    private M message;

    public IncomingDatagramMessage(InetAddress sourceAddress, int sourcePort, M message) {
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.message = message;
    }

    public InetAddress getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(InetAddress sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public M getMessage() {
        return message;
    }

    public void setMessage(M message) {
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        return "IncomingDatagramMessage{" +
                "sourceAddress=" + sourceAddress +
                ", sourcePort=" + sourcePort +
                ", message=" + message +
                '}';
    }
}
