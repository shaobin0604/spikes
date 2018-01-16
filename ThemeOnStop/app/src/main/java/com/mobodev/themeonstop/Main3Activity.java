package com.mobodev.themeonstop;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class Main3Activity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }
}
