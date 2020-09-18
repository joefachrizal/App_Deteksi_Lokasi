package com.example.appdeteksilokasi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.appdeteksilokasi.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices
import java.util.*
import kotlin.math.*

class MainActivity : AppCompatActivity() {
    // lokasi statis berdasarkan Latitude dan Longitude -6.597723, 106.799559
    private val baseLatitude = -6.597723
    private val baseLongitude = 106.799559

    companion object {
        const val ID_LOCATION_PERMISSION = 0
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissionLocation()
        onClick()
    }


    private fun onClick() {
        binding.fabCheckIn.setOnClickListener {
            startScanLocation()
            Handler(Looper.getMainLooper()).postDelayed({
                getLocationCoordinat()
            }, 4000)
        }
    }

    private fun startScanLocation() {
        with(binding) {
            rippleBackground.startRippleAnimation()
            tvScanning.visibility = View.VISIBLE
            tvCheckInSuccess.visibility = View.GONE
        }
    }

    private fun stopScanLocation() {
        with(binding) {
            rippleBackground.stopRippleAnimation()
            tvScanning.visibility = View.GONE
        }
    }

    private fun getLocationCoordinat() {
        if (checkPermission()) {
            if (isLoactionEnable()) {
                LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location ->
                    val currentLat = location.latitude
                    val currentLong = location.longitude

                    val resultCoordinat = "$currentLat, $currentLong"

                    val distance = calculateDistance(
                        currentLat, currentLong,
                        baseLatitude, baseLongitude
                    ) * 1000

                    val jarak = "${distance.toInt()} meter"

                    Log.d("posisi titik", "lokasi $baseLatitude, $baseLongitude")
                    Log.d("posisi hp", "lokasi $resultCoordinat")

                    getAddressesLatLot(currentLat, currentLong)

                    with(binding) {
                        tvCheckInSuccess.visibility = View.VISIBLE
                        tvCheckInSuccess.text = jarak
                    }

                    stopScanLocation()
                }
            } else {
                showTurnOnLocation()
            }
        } else {
            reqPermission()
        }
    }

    private fun getAddressesLatLot(lat: Double, lon: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            val address = addresses[0].getAddressLine(0)
            Log.d("alamat", "lokasi $address")
            binding.tvDaerah.text = address

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("SameParameterValue")
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6372.8 // in kilometers

        val radiansLat1 = Math.toRadians(lat1)
        val radiansLat2 = Math.toRadians(lat2)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        return 2 * r * asin(
            sqrt(
                sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(radiansLat1) * cos(
                    radiansLat2
                )
            )
        )
    }

    override fun onRequestPermissionsResult(
        reqCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(reqCode, permissions, grantResults)
        if (reqCode == ID_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Akses diizinkan", Toast.LENGTH_SHORT).show()
                if (!isLoactionEnable()) {
                    showTurnOnLocation()
                }
            } else {
                Toast.makeText(this, "Akses Tidak diizinkan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionLocation() {
        if (checkPermission()) {
            if (!isLoactionEnable()) {
                showTurnOnLocation()
            }
        } else {
            reqPermission()
        }
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLoactionEnable(): Boolean {
        val locManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            return true
        }
        return false
    }

    private fun reqPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), ID_LOCATION_PERMISSION
        )
    }

    private fun showTurnOnLocation() {
        Toast.makeText(this, "Silahakan Aktifkan Lokasi Kamu", Toast.LENGTH_SHORT).show()
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}