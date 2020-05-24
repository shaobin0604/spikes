package com.mobodev.spikes.ssdp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mobodev.spikes.ssdp.databinding.ActivityDeviceBinding;

public class DeviceActivity extends AppCompatActivity {
    private ActivityDeviceBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDeviceBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }
}
