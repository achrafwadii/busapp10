package com.example.busapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.floatSetOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var fauth :FirebaseAuth
    private lateinit var fstore : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {

        fauth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Get UI elements
        val profileImage = findViewById<ImageView>(R.id.profile_image)




        val user = fauth.currentUser
        val nomCompletTxtV :TextView = findViewById(R.id.user_name)
        val emailTxtV : TextView = findViewById(R.id.user_email)
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






        val backButton: ImageButton = findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Go back to the previous activity
        }

    }
}
