package com.example.spinningcat.activities

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.spinningcat.R

class VideoPlayer : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var tvWorkoutName: TextView
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        webView = findViewById(R.id.webView)
        tvWorkoutName = findViewById(R.id.tvWorkoutName)
        btnVolver = findViewById(R.id.btnVolver)

        // Obtener datos del intent
        val videoUrl = intent.getStringExtra("VIDEO_URL") ?: ""
        val workoutName = intent.getStringExtra("WORKOUT_NAME") ?: "Video"

        tvWorkoutName.text = workoutName

        if (videoUrl.isEmpty()) {
            Toast.makeText(this, "No hay video disponible", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar WebView
        setupWebView(videoUrl)

        // Botón volver
        btnVolver.setOnClickListener {
            finish()
        }

        // ✅ Manejar el botón back con OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupWebView(videoUrl: String) {
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // Convertir URL de YouTube a formato embebido
        val embedUrl = convertToEmbedUrl(videoUrl)

        // Cargar video en iframe HTML
        val html = """
            <html>
            <body style="margin:0;padding:0;">
                <iframe width="100%" height="100%" 
                    src="$embedUrl" 
                    frameborder="0" 
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                    allowfullscreen>
                </iframe>
            </body>
            </html>
        """.trimIndent()

        webView.loadData(html, "text/html", "utf-8")
    }

    private fun convertToEmbedUrl(url: String): String {
        // Convertir URL de YouTube normal a formato embed
        return when {
            url.contains("youtube.com/watch?v=") -> {
                val videoId = url.substringAfter("watch?v=").substringBefore("&")
                "https://www.youtube.com/embed/$videoId"
            }
            url.contains("youtu.be/") -> {
                val videoId = url.substringAfter("youtu.be/").substringBefore("?")
                "https://www.youtube.com/embed/$videoId"
            }
            else -> url // Si ya está en formato embed o es otra URL
        }
    }
}