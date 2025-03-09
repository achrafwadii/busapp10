package com.example.busapp


import NominatimApi
import NominatimResult
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.config.Configuration

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.views.overlay.Polyline
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var fauth :FirebaseAuth
    private lateinit var fstore : FirebaseFirestore
    private lateinit var mapView: MapView






    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        fauth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        mapView = findViewById(R.id.mapView)

        // Configuration de OSMDroid

        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

        // Initialisation de la carte
        val mapView = findViewById<MapView>(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)


        val guelmim = GeoPoint(28.9884, -10.0574)
        val mapController = mapView.controller
        mapController.setZoom(16.0)
        mapController.setCenter(guelmim)


        val marker = Marker(mapView)
        marker.position = guelmim
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Guelmim"
        mapView.overlays.add(marker)

        val drawable = ContextCompat.getDrawable(this, R.drawable.bus) as BitmapDrawable
        val originalBitmap = drawable.bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 20, 20, false)

        val smallMarker = BitmapDrawable(resources, resizedBitmap)
        marker.icon = smallMarker


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        mapView.overlays.add(locationOverlay)

        val gpsButton = findViewById<Button>(R.id.gpsButton)

        gpsButton.setOnClickListener {
            val currentLocation: GeoPoint? = locationOverlay.myLocation
            if (currentLocation != null) {
                val userGeoPoint = GeoPoint(currentLocation.latitude, currentLocation.longitude)
                mapView.controller.animateTo(userGeoPoint)
                mapView.controller.setZoom(18.0)
            } else {
                Toast.makeText(this, "Localisation non disponible", Toast.LENGTH_SHORT).show()
            }
        }





// Show the popup when a button is clicked
        findViewById<View>(R.id.showPopupButton).setOnClickListener {
            showRouteSelectorPopup()
        }


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

        val headerView: View = navigationView.getHeaderView(0)
        val user = fauth.currentUser
        val nomCompletTxtV :TextView = headerView.findViewById(R.id.headerTitle)
        val emailTxtV : TextView = headerView.findViewById(R.id.headerSubtitle)
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



    }
    private fun geocodeAddress(address: String, callback: (GeoPoint?) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val nominatimApi = retrofit.create(NominatimApi::class.java)

        nominatimApi.search(address).enqueue(object : Callback<List<NominatimResult>> {
            override fun onResponse(
                call: Call<List<NominatimResult>>,
                response: Response<List<NominatimResult>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!
                    if (results.isNotEmpty()) {
                        val firstResult = results[0]
                        val geoPoint = GeoPoint(firstResult.lat, firstResult.lon)
                        callback(geoPoint)
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<NominatimResult>>, t: Throwable) {
                callback(null)
            }
        })
    }



    private var roadOverlay: Polyline? = null // Stocker l'itinéraire actuel

    private fun drawRoute(source: GeoPoint, destination: GeoPoint) {
        val roadManager = OSRMRoadManager(this, "MyAppUserAgent")
        val waypoints = arrayListOf(source, destination)

        Thread {
            val road = roadManager.getRoad(waypoints)
            runOnUiThread {
                if (road.mStatus != Road.STATUS_OK) {
                    Toast.makeText(this, "Impossible de récupérer l'itinéraire", Toast.LENGTH_SHORT).show()
                } else {
                    roadOverlay?.let {
                        mapView.overlays.remove(it)
                    }

                    roadOverlay = RoadManager.buildRoadOverlay(road).apply {
                        outlinePaint.color = Color.RED
                        outlinePaint.strokeWidth = 8f
                    }
                    mapView.overlays.add(roadOverlay)
                    mapView.invalidate()
                }
            }
        }.start()
    }




    @SuppressLint("MissingInflatedId")

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
        val listViewResults =view.findViewById<ListView>(R.id.listViewResults)
        val db= Firebase.firestore
        val originalArretsList = mutableListOf<String>()
        val displayedArretsList = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayedArretsList)
        listViewResults.adapter = adapter
        // Fonction pour récupérer les arrêts depuis Firestore
        fun loadBusStops() {
            db.collection("pointarret") // Remplace par ton nom de collection
                .get()
                .addOnSuccessListener { documents ->
                    originalArretsList.clear() // Nettoyer avant d'ajouter les nouvelles données
                    for (document in documents) {
                        val source = document.getString("address")
                        if (!source.isNullOrEmpty()) originalArretsList.add(source)
                    }
                    displayedArretsList.clear()
                    displayedArretsList.addAll(originalArretsList)

                    adapter.notifyDataSetChanged() // Mettre à jour la liste
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur de chargement: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        loadBusStops()
        fun updateList(query: String) {
            val filteredCities = originalArretsList.filter { it.contains(query, ignoreCase = true) }
            displayedArretsList.clear()
            displayedArretsList.addAll(filteredCities)
            adapter.notifyDataSetChanged()
        }


        // Recherche en temps réel pour source
        sourceInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateList(s.toString())
            }
        })
        // Recherche en temps réel pour destination
        destinationInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateList(s.toString())
            }
        })
        // Remplir les champs quand on clique sur une ville
        listViewResults.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = listViewResults.adapter.getItem(position) as String
            if (sourceInput.hasFocus()) {
                sourceInput.setText(selectedCity)
            } else if (destinationInput.hasFocus()) {
                destinationInput.setText(selectedCity)
            }
        }




        // Handle Cancel button click
        
        // Handle Search button click
        cancelButton.setOnClickListener {
            val source = sourceInput.text.toString()
            val destination = destinationInput.text.toString()
            if (source.isEmpty() || destination.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer une source et une destination", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, item_itineraire::class.java)
                intent.putExtra("src",source)
                intent.putExtra("dst",destination)
                startActivity(intent)
            }



        }

        fun isAddressInGuelmimOuedNoun(lat: Double, lon: Double): Boolean {
            val minLat = 28.9
            val maxLat = 29.6
            val minLon = -10.3
            val maxLon = -9.2

            return lat in minLat..maxLat && lon in minLon..maxLon
        }

        searchButton.setOnClickListener {
            val sourceText = sourceInput.text.toString()
            val destinationText = destinationInput.text.toString()

            if (sourceText.isNotEmpty() && destinationText.isNotEmpty()) {
                geocodeAddress(sourceText) { sourceGeo ->
                    if (sourceGeo != null && isAddressInGuelmimOuedNoun(sourceGeo.latitude, sourceGeo.longitude)) {
                        geocodeAddress(destinationText) { destGeo ->
                            if (destGeo != null && isAddressInGuelmimOuedNoun(destGeo.latitude, destGeo.longitude)) {
                                drawRoute(sourceGeo, destGeo)
                                dialog.dismiss()
                            } else {
                                Toast.makeText(this, "Adresse de destination hors de Guelmim-Oued Noun", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Adresse source hors de Guelmim-Oued Noun", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Entrez une source et une destination", Toast.LENGTH_SHORT).show()
            }
        }



    }


    // Handle Menu Item Clicks
    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {

            R.id.nav_home -> {


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
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)

            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                // Redirect to Login Activity (or close the app)
                val intent = Intent(this, login::class.java)
                startActivity(intent)
                finish()  // Close the MainActivity after logging out
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
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