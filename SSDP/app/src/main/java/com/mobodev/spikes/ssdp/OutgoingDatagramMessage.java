package com.mobodev.spikes.ssdp;

import androidx.annotation.NonNull;

import java.net.InetAddress;

public class OutgoingDatagramMessage<M extends Message> {
    private InetAddress destinationAddress;
    private int destinationPort;
    private M message;

    public OutgoingDatagramMessage(InetAddress destinationAddress, int destinationPort, M message) {
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
        this.message = message;
    }

    public InetAddress getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(InetAddress destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
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
        return "OutgoingDatagramMessage{" +
                "destinationAddress=" + destinationAddress +
                ", destinationPort=" + destinationPort +
                ", message=" + message +
                '}';
    }
}
