package com.example.busapp

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private var mGoogleMap: GoogleMap? = null
    private lateinit var drawerLayout: DrawerLayout
    // Handle Map Ready
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        googleMap.uiSettings.isRotateGesturesEnabled = false

        // Define the bounding box for Guelmim-Oued Noun
        val guelmimBounds = LatLngBounds(
            LatLng(28.96, -10.07), // Southwest corner
            LatLng(29.02, -10.03)   // Northeast corner
        )

        // Move the camera inside the bounds first
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(guelmimBounds, 50))

        // Restrict the camera view to the defined bounds
        googleMap.setLatLngBoundsForCameraTarget(guelmimBounds)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guelmimBounds.center, 8f))

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Hide the title (so only the hamburger icon appears)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Initialize Drawer Layout
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        // Add Toggle for Drawer
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Ensure the hamburger icon is visible
        toggle.isDrawerIndicatorEnabled = true  // 🔹 Ensures the hamburger icon appears
        supportActionBar?.setDisplayHomeAsUpEnabled(false) // 🔹 Removes back button
    }




    // Handle Menu Item Clicks
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_routes -> {
                Toast.makeText(this, "Routes", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_schedule -> {
                Toast.makeText(this, "Schedule", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_rateUs -> {
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Handle Back Press
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}