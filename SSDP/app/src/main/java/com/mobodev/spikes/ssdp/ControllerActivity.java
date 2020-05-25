package com.mobodev.spikes.ssdp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.mobodev.spikes.ssdp.databinding.ActivityControllerBinding;

public class ControllerActivity extends AppCompatActivity {
    private static final int SAMPLE_COUNT = 100;
    private static final int MAX_WAIT_SECOND = 120;

    private static final String TAG = "ControllerActivity";

    private ActivityControllerBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityControllerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Globals.init(this, false);

        final SampleData sampleData = new SampleData(SAMPLE_COUNT);
        Globals.getInstance().setSampleData(sampleData);

        try {
            Globals.getInstance().getRouter().enable();
        } catch (RouterException e) {
            Log.e(TAG, "router enable error", e);
        }

        mBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.btnSearch.setEnabled(false);
                sampleData.start();
                Globals.getInstance().getDefaultExecutorService().execute(new SendingSearch(3) {
                    @Override
                    public int getBulkRepeat() {
                        return SAMPLE_COUNT;
                    }

                    @Override
                    protected void prepareOutgoingSearchRequest(OutgoingDatagramMessage<RequestMessage> message) {
                        sampleData.putSendTs((int) message.getMessage().id, SystemClock.elapsedRealtime());
                    }
                });
                mBinding.btnSearch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sampleData.stop();
                        final SampleData.TestResult result = sampleData.calcuate();
                        mBinding.tvLog.append(result.toString() + "\n");
                        mBinding.btnSearch.setEnabled(true);
                    }
                }, MAX_WAIT_SECOND * 1000L);
            }
        });


    }
}
