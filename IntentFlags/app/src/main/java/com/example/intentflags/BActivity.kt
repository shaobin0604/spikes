package com.example.intentflags

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, CActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP))
        finish()
    }
}