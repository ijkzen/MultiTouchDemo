package com.example.multitouchtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val captureView = findViewById<ThreeFingerCaptureView>(R.id.capture)
        captureView.setActivity(this)
    }
}