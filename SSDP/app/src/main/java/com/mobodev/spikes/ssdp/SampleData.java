package com.mobodev.spikes.ssdp;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class SampleData {
    private int mCount;

    private long[] mSendTs;
    private long[] mReceiveTs;

    private boolean mRunning;

    public SampleData(int count) {
        mCount = count;
        mSendTs = new long[mCount];
        mReceiveTs = new long[mCount];
    }

    public synchronized void reset() {
        mRunning = false;
        Arrays.fill(mSendTs, 0);
        Arrays.fill(mReceiveTs, 0);
    }

    public synchronized void start() {
        mRunning = true;
    }

    public synchronized void stop() {
        mRunning = false;
    }

    public synchronized void putSendTs(int id, long ts) {
        if (mRunning) {
            mSendTs[id - 1] = ts;
        }
    }

    public synchronized void putReceiveTs(int id, long ts) {
        if (mRunning) {
            mReceiveTs[id - 1] = ts;
        }
    }

    public synchronized TestResult calcuate() {
        int repliedCount = 0;
        int totalLatency = 0;
        for (int i = 0; i < mCount; i++) {
            if (mReceiveTs[i] > 0) {
                repliedCount++;
                if (mSendTs[i] > 0 && mReceiveTs[i] > mSendTs[i]) {
                    totalLatency += mReceiveTs[i] - mSendTs[i];
                }
            }
        }

        int lossRate = (mCount - repliedCount) * 100 / mCount;
        int avgLatency = repliedCount > 0 ? totalLatency / repliedCount : Integer.MIN_VALUE;

        return new TestResult(lossRate, avgLatency);
    }

    public static class TestResult {
        // 丢包率
        public int lossRate;
        // 平均延迟
        public int avgLatency;

        public TestResult(int lossRate, int avgLatency) {
            this.lossRate = lossRate;
            this.avgLatency = avgLatency;
        }

        @NonNull
        @Override
        public String toString() {
            return "TestResult{" +
                    "lossRate=" + lossRate +
                    "%, avgLatency=" + avgLatency +
                    "ms}";
        }
    }
}
