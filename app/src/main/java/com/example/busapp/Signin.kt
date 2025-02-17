package com.example.busapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isNotEmpty
import com.example.busapp.ui.theme.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class signup : AppCompatActivity() {
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        val nomtxt: TextView = findViewById(R.id.nom)
        val pnomtxt : TextView = findViewById(R.id.prenom)
        val agetxt : TextView = findViewById(R.id.age)
        val genre : RadioGroup = findViewById(R.id.sex)
        val male = R.id.male
        val female = R.id.female
        val signup_button: Button = findViewById(R.id.signup_button)
        val emailtxt: TextView = findViewById(R.id.sgemail)
        val passwordtxt: TextView = findViewById(R.id.signup_password)
        val confpasswordtxt: TextView = findViewById(R.id.signup_confirm)
        val toLogin: TextView = findViewById(R.id.loginRedirectText)
        firestore = FirebaseFirestore.getInstance()
        firebaseauth = FirebaseAuth.getInstance()
        signup_button.setOnClickListener {
            var nom: String = nomtxt.text.toString()
            var prenom : String = pnomtxt.text.toString()
            var age : String = agetxt.text.toString()
            var email: String = emailtxt.text.toString().trim()
            var password: String = passwordtxt.text.toString()
            var confpassword: String = confpasswordtxt.text.toString()
            var sex : String =""
            if(genre.checkedRadioButtonId == male) sex = "male"
            if(genre.checkedRadioButtonId == female) sex ="female"

            if (fieldsNotEmpty(nom , prenom , age , email , password , confpassword , genre)) {
                if (password.equals(confpassword)) {
                    firebaseauth.createUserWithEmailAndPassword(email, password).
                    addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            val currentUser = user(prenom , nom , email , age , sex)
                            currentUser.createUser(this , firebaseauth , firestore)
                            val intent = Intent(this, login::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else {
                    Toast.makeText(this, "password dosent match confirmation", Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(this,"please enter your informations correctly", Toast.LENGTH_SHORT).show()
            }
        }
        toLogin.setOnClickListener {
            val intent = Intent(this , login::class.java)
            startActivity(intent)
        }
    }

    fun fieldsNotEmpty(nom : String , pnom : String , age : String , email : String , password : String , confpassword : String , sex : RadioGroup):Boolean{
        if(nom.isNotEmpty()&& pnom.isNotEmpty() && age.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confpassword.isNotEmpty() && sex.checkedRadioButtonId != -1){
            return true
        }else{
            return false
        }
    }
}