package com.example.spinningcat.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spinningcat.R

class VideoPlayer : AppCompatActivity() {

    private lateinit var tvWorkoutName: TextView
    private lateinit var tvInstrucciones: TextView
    private lateinit var btnAbrirYoutube: Button
    private lateinit var btnVolver: Button
    private lateinit var ivIcono: ImageView
    private var videoUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        tvWorkoutName = findViewById(R.id.tvWorkoutName)
        tvInstrucciones = findViewById(R.id.tvInstrucciones)
        btnAbrirYoutube = findViewById(R.id.btnAbrirYoutube)
        btnVolver = findViewById(R.id.btnVolver)
        ivIcono = findViewById(R.id.ivIcono)

        // Obtener datos del intent
        videoUrl = intent.getStringExtra("VIDEO_URL") ?: ""
        val workoutName = intent.getStringExtra("WORKOUT_NAME") ?: "Video"

        tvWorkoutName.text = workoutName

        if (videoUrl.isEmpty()) {
            Toast.makeText(this, "No hay video disponible", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Boton abrir en YouTube
        btnAbrirYoutube.setOnClickListener {
            abrirEnYoutube(videoUrl)
        }

        // Boton volver
        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun abrirEnYoutube(url: String) {
        try {
            // Intentar abrir en la app de YouTube
            val videoId = extraerVideoId(url)
            val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
            startActivity(intentApp)
        } catch (e: Exception) {
            // Si no tiene la app, abrir en navegador
            val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intentBrowser)
        }
    }

    private fun extraerVideoId(url: String): String {
        return when {
            url.contains("youtube.com/watch?v=") -> {
                url.substringAfter("watch?v=").substringBefore("&")
            }
            url.contains("youtu.be/") -> {
                url.substringAfter("youtu.be/").substringBefore("?")
            }
            url.contains("youtube.com/embed/") -> {
                url.substringAfter("embed/").substringBefore("?")
            }
            else -> url
        }
    }
}