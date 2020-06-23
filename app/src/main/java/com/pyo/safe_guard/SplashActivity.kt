package com.pyo.safe_guard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.SystemClock


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SystemClock.sleep(1000)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}