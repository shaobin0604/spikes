package com.mobodev.spikes.ssdp;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Globals {
    private static volatile Globals sGlobals;

    final private ExecutorService defaultExecutorService;

    final private DatagramProcessor datagramProcessor;

    final private Router router;

    final private Context context;

    final private boolean isDevice;

    private SampleData mSampleData;

    private Globals(@NonNull Context context, boolean isDevice) {
        this.context = context.getApplicationContext();
        defaultExecutorService = Executors.newCachedThreadPool();
        datagramProcessor = new DatagramProcessor();
        this.isDevice = isDevice;
        router = new AndroidRouter(this.context, isDevice);
    }

    public SampleData getSampleData() {
        return mSampleData;
    }

    public void setSampleData(SampleData sampleData) {
        mSampleData = sampleData;
    }

    /**
     * Call this method in {@link Application#onCreate()}
     *
     * @param context
     * @return the singleton {@link Globals} instance
     */
    public static void init(@NonNull Context context, boolean isDevice) {
        if (sGlobals == null) {
            synchronized (Globals.class) {
                if (sGlobals == null) {
                    sGlobals = new Globals(context, isDevice);
                }
            }
        }
    }

    public static Globals getInstance() {
        return sGlobals;
    }

    public ExecutorService getDefaultExecutorService() {
        return defaultExecutorService;
    }

    public DatagramProcessor getDatagramProcessor() {
        return datagramProcessor;
    }

    public Router getRouter() {
        return router;
    }

    public Context getContext() {
        return context;
    }
}
