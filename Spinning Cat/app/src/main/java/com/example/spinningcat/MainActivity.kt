package com.example.spinningcat

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import android.widget.Spinner
import android.widget.AdapterView
import java.util.Locale
import androidx.core.content.edit
import com.example.spinningcat.activities.Register
import com.example.spinningcat.activities.SpinnerAdapter
import com.example.spinningcat.activities.Login

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
        window.navigationBarColor =
            getColor(R.color.bgColor) // Cambia el color de la barra de navegación

        // el textview donde se muestra el TOS y PP
        val textView = findViewById<TextView>(R.id.TOS_PP)

        // Habilitar los enlaces en el TextView
        textView.movementMethod = LinkMovementMethod.getInstance()

        // Botón para ir a la pantalla de Registro
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            val intent = Intent(applicationContext, Register::class.java)
            startActivity(intent)
        }

        // Botón para ir a la pantalla de Login
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
        }


        // Spinner (menu desplegable) personalizado para seleccionar el idioma
        // NO PREGUNTAR NUNCA COMO FUNCIONA PORQUE NO LO SE
        // FUNCIONA GRACIAS A LA FÉ, LA SUERTE Y ALGUNAS DE MIS LAGRIMAS
        // SI LO ENTIENDES NO FUNCIONA Y SI FUNCIONA NO LO ENTIENDES
        // EDITAR BAJO TU PROPIO RIESGO
        // NO ME HAGO RESPONSABLE DE FUTUROS CRASHEOS SI SE EDITA
        // Fdo.: Yery :)
        val idiomas = listOf(
            R.drawable.icono_espanita_foreground,
            R.drawable.icono_inglish_pitinglish_foreground
        )
        val spinner = findViewById<Spinner>(R.id.idiomas)
         spinner.adapter = SpinnerAdapter(this, idiomas)

        @Suppress("DEPRECATION")
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val prefs = getSharedPreferences("settings", MODE_PRIVATE)
                val selectedLang = if (position == 0) "es" else "en"
                if (prefs.getString("lang", "") != selectedLang) {
                    prefs.edit { putString("lang", selectedLang) }
                    val locale = Locale(selectedLang)
                    val config = resources.configuration
                    config.setLocale(locale)
                    resources.updateConfiguration(config, resources.displayMetrics)
                    recreate()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // no hace nada pero es necesario para el override
            }
        }
    }
}
    
