package com.example.spinningcat

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imageViewGif = findViewById<ImageView>(R.id.imageViewGif)
        Glide.with(this)
            .asGif()
            .load(R.drawable.oiia_cat) // Reemplaza con el nombre de tu archivo GIF en drawable
            .into(imageViewGif)

        @Suppress("DEPRECATION")
        window.navigationBarColor = getColor(R.color.bgColor) // Cambia el color de la barra de navegación

        // the TextView from the layout file
        val textView = findViewById<TextView>(R.id.TOS_PP)

        // Finding and displaying the content
        // that consists a URL as a hyperlink
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}
