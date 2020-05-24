package com.mobodev.spikes.ssdp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Globals {

    final private ExecutorService defaultExecutorService;

    final private DatagramProcessor datagramProcessor;

    private Globals() {
        defaultExecutorService = Executors.newCachedThreadPool();
        datagramProcessor = new DatagramProcessor();


    }
}
