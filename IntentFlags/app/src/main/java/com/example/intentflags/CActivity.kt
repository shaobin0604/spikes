package com.example.intentflags

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_c.*

class CActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c)
        button2.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra("result", "result from CActivity"))
            finish()
        }
    }
}