package com.mobodev.spikes.ssdp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mobodev.spikes.ssdp.databinding.ActivityControllerBinding;

public class ControllerActivity extends AppCompatActivity {

    private ActivityControllerBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityControllerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }
}
