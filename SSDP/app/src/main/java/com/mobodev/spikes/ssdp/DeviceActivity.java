package com.mobodev.spikes.ssdp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobodev.spikes.ssdp.databinding.ActivityDeviceBinding;

public class DeviceActivity extends AppCompatActivity {
    private ActivityDeviceBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDeviceBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Globals.init(this, true);

        mBinding.btnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(DeviceActivity.this, R.string.enablingRouter, Toast.LENGTH_SHORT).show();
                    Globals.getInstance().getRouter().enable();
                } catch (RouterException ex) {
                    Toast.makeText(DeviceActivity.this, getText(R.string.errorSwitchingRouter) + ex.toString(), Toast.LENGTH_LONG).show();
                    ex.printStackTrace(System.err);
                }
            }
        });

        mBinding.btnStopServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(DeviceActivity.this, R.string.disablingRouter, Toast.LENGTH_SHORT).show();
                    Globals.getInstance().getRouter().disable();
                } catch (RouterException ex) {
                    Toast.makeText(DeviceActivity.this, getText(R.string.errorSwitchingRouter) + ex.toString(), Toast.LENGTH_LONG).show();
                    ex.printStackTrace(System.err);
                }
            }
        });
    }
}
