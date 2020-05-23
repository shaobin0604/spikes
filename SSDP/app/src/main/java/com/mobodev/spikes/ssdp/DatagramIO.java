package com.mobodev.spikes.ssdp;

import java.net.MulticastSocket;

public class DatagramIO implements Runnable {

    protected MulticastSocket socket; // For sending unicast & multicast, and reveiving unicast

    public DatagramIO() {

    }

    @Override
    public void run() {

    }
}
