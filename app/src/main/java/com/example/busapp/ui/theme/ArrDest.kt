package com.example.busapp.ui.theme

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.busapp.R

class ArrDest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_arr_dest)

        val mainLayout = findViewById<LinearLayout>(R.id.main)


// Dynamically change size
        val params = mainLayout.layoutParams
        params.width = 500 // Set custom width in pixels
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        mainLayout.layoutParams = params

    }
}