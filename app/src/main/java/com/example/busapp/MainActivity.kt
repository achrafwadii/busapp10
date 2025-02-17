package com.example.busapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.common.api.Response
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity(), OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private var mGoogleMap: GoogleMap? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var fauth :FirebaseAuth
    private lateinit var fstore : FirebaseFirestore

    // Handle Map Ready
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        googleMap.uiSettings.isRotateGesturesEnabled = false

        // Define the bounding box for Guelmim-Oued Noun
        val guelmim = LatLng(28.98, -10.07)
        mGoogleMap?.addMarker(MarkerOptions().position(guelmim).title("Guelmim"))
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(guelmim, 12f))





    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        fauth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()




// Show the popup when a button is clicked
        findViewById<View>(R.id.showPopupButton).setOnClickListener {
            showRouteSelectorPopup()
        }



        // Initialize the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

    private fun showRouteSelectorPopup() {
        // Inflate the layout for the BottomSheetDialog
        val view = LayoutInflater.from(this).inflate(R.layout.activity_arr_dest, null)

        // Create the BottomSheetDialog
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.show()

        // Find views inside the BottomSheetDialog
        val sourceInput = view.findViewById<EditText>(R.id.sourceInput)
        val destinationInput = view.findViewById<EditText>(R.id.destinationInput)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val searchButton = view.findViewById<Button>(R.id.searchButton)

        // Handle Cancel button click
        cancelButton.setOnClickListener {
            dialog.dismiss() // Close the dialog
        }

        // Handle Search button click
        searchButton.setOnClickListener {
            val source = sourceInput.text.toString()
            val destination = destinationInput.text.toString()

            // Dismiss the dialog after searching
            dialog.dismiss()
        }
    }








    // Handle Menu Item Clicks
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val user = fauth.currentUser
        val nomCompletTxtV :TextView = findViewById(R.id.headerTitle)
        val emailTxtV : TextView = findViewById(R.id.headerSubtitle)
        if (user != null) {
            val userId = user.uid
            val docref = fstore.collection("users").document(userId)
            docref.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this,"${error.message}" , Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val nom = snapshot.getString("nom").toString()
                    val prenom = snapshot.getString("prenom").toString()
                    val email = snapshot.getString("email").toString()
                    nomCompletTxtV.text = nom +" "+prenom
                    emailTxtV.text = email
                }
            } }
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