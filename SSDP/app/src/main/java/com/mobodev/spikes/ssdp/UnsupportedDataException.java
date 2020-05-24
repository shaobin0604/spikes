package com.mobodev.spikes.ssdp;

public class UnsupportedDataException extends Exception {

    public UnsupportedDataException() {
    }

    public UnsupportedDataException(String message) {
        super(message);
    }

    public UnsupportedDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedDataException(Throwable cause) {
        super(cause);
    }
}
