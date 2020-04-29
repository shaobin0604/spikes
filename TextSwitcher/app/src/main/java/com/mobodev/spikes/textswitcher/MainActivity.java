package com.mobodev.spikes.textswitcher;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextSwitcher textSwitcher;
    private int count = 0;
    private Animation mAnimation;
    private TextView mTvNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);

        textSwitcher.setCurrentText("Hello Android App Developer");

        Animation textAnimationIn = AnimationUtils.loadAnimation(this, R.anim.in);
        Animation textAnimationOut = AnimationUtils.loadAnimation(this, R.anim.out);

        textSwitcher.setInAnimation(textAnimationIn);
        textSwitcher.setOutAnimation(textAnimationOut);

        mTvNumber = findViewById(R.id.tv_number);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.in_out);

        findViewById(R.id.btn_increase_number).setOnClickListener(new View.OnClickListener() {
            private int number = 0;
            @Override
            public void onClick(View v) {
                mTvNumber.setText(String.valueOf(number++));
                mTvNumber.startAnimation(mAnimation);
            }
        });
    }

    public void showNextText(View view) {
        count++;
        textSwitcher.setText(String.valueOf(count));
    }
}
