package com.example.busapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class MainActivity : AppCompatActivity(),OnMapReadyCallback {
    private var mGoogleMap:GoogleMap?  = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        googleMap.uiSettings.isRotateGesturesEnabled = false

        val guelmimBounds = LatLngBounds(
            LatLng(28.0, -10.5), // Southwest corner
            LatLng(30.0, -8.5)   // Northeast corner
        )

        // Restrict the camera to Guelmim-Oued Noun's bounds
        googleMap.setLatLngBoundsForCameraTarget(guelmimBounds)

        // Move the camera to Guelmim-Oued Noun and set an appropriate zoom level
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guelmimBounds.center, 8f))
    }
    }
