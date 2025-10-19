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
import com.example.spinningcat.adapter.SpinnerAdapter
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

        // Cambia la foto del imageview por el gif del gatete
        val imageViewGif = findViewById<ImageView>(R.id.imageViewGif)
        Glide.with(this)
            .asGif()
            .load(R.drawable.oiia_cat)
            .into(imageViewGif)

        @Suppress("DEPRECATION")
        window.navigationBarColor =
            getColor(R.color.bgColor) // Cambia el color de la barra de navegación

        // el textview donde se muestra el TOS y PP (Terms of Service y Privacy Policy)
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

        val idiomaMovil = Locale.getDefault().language
        val idiomas = listOf(
            R.drawable.icono_espanita_foreground, // posicion 0
            R.drawable.icono_inglish_pitinglish_foreground // posicion 1
        )
        val initialPos = if (idiomaMovil == "es") 0 else 1
        val spinner = findViewById<Spinner>(R.id.idiomas)
         spinner.adapter = SpinnerAdapter(this, idiomas)
        spinner.setSelection(initialPos)

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
                // no hace nada pero es necesario para el override de arriba
            }
        }
    }

    // para cerrar la main activity desde otra actividad
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra("finish_main", false)) {
            finish()
        }
    }
}
    
