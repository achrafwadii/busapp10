package com.example.busapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {
    private lateinit var firebaseauth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseauth = FirebaseAuth.getInstance()
        setContentView(R.layout.login)
        val loginbtn : Button = findViewById(R.id.login_button)
        val emailtxt : TextView = findViewById(R.id.login_email)
        val passwordtxt : TextView = findViewById(R.id.login_password)
        val toSignup : TextView = findViewById(R.id.signupRedirectText)
        val stay_visitor : TextView = findViewById(R.id.stayasvisitor)
        loginbtn.setOnClickListener{
            val email = emailtxt.text.toString().trim()
            val password = passwordtxt.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseauth.signInWithEmailAndPassword(email , password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent2 = Intent(this, MainActivity::class.java)
                        startActivity(intent2)
                        finish()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }

        }
        toSignup.setOnClickListener{
            val intent1 = Intent(this, signup::class.java)
            startActivity(intent1)

        }
        stay_visitor.setOnClickListener{
            val intent3 = Intent(this, MainActivity::class.java)
            startActivity(intent3)
            finish()
        }

    }

}