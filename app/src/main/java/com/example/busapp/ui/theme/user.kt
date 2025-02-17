package com.example.busapp.ui.theme

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class user{
    lateinit var userId: String
    var prenom: String
    var nom: String
    var email: String
    var age: String
    var sex: String
    constructor(
        prenom: String,
        nom: String,
        email: String,
        age: String,
        sex: String){
        this.prenom = prenom
        this.nom = nom
        this.email = email
        this.age = age
        this.sex = sex
    }
    fun createUser(currentActivity : AppCompatActivity, fauth : FirebaseAuth, fstore : FirebaseFirestore){
        val user = fauth.currentUser
        userId = user?.uid?:""
        val user1 : Map<String , String> = mapOf("prenom" to prenom ,"nom" to nom,"age" to age , "sex" to sex ,"email" to email)
        val database : DocumentReference = fstore.collection("users").document(userId)
        database.set(user1).addOnSuccessListener{
            Toast.makeText(currentActivity , "signup successfuly", Toast.LENGTH_SHORT).show()
        }
    }
}