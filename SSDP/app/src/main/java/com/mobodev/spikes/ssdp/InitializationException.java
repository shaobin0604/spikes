package com.mobodev.spikes.ssdp;

public class InitializationException extends RuntimeException {

    public InitializationException(String s) {
        super(s);
    }

    public InitializationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
