package com.example.appdeteksilokasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.appdeteksilokasi.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClick()
    }

    private fun onClick() {
        fabCheckIn.setOnClickListener {
            startScanLocation()
            Handler(Looper.getMainLooper()).postDelayed({
                stopScanLocation()
            }, 4000)
        }
    }

    private fun startScanLocation() {
        rippleBackground.startRippleAnimation()
        tvScanning.visibility = View.VISIBLE
        tvCheckInSuccess.visibility = View.GONE
    }

    private fun stopScanLocation() {
        rippleBackground.stopRippleAnimation()
        tvScanning.visibility = View.GONE
    }

}