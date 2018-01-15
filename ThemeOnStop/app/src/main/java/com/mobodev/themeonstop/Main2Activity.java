package com.mobodev.themeonstop;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class Main2Activity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}
