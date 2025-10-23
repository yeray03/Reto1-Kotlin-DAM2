package com.example.spinningcat

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spinningcat.activities.Register
import com.example.spinningcat.adapter.SpinnerAdapter
import com.example.spinningcat.activities.Login
import com.example.spinningcat.room.RoomDB
import com.example.spinningcat.room.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.toString

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


        // -----------------------
        // The ROOM database does not exist when you installs de APP in the mobile
        // It is technically created when you access it for the first time. Also,
        // it is empty. So we preload it with some random data...

        // The sole instance of db
        val db = RoomDB(this)

        // We launch this part as a Coroutine. This means, a thread parallel to the Activity.
        // More or less. If we do like this, we can then update the UI easily, and this thread
        // dies whenever the Activity also dies
        lifecycleScope.launch(Dispatchers.IO) {
            // We get the EnterpriseDao and then call to getAll method
            val list = db.getUserDao().getAll()
            if (list.isEmpty()) {

                // Empty, so we add a few...
                db.getUserDao().insertAll(
                    User(nickname = "prueba01",nombre = "pruebaRoom", apellidos = "si", contrasena = "123", email = "prueba.com", fechaNacimiento = "11/11/1111", tipoUsuario = 0, nivel = 2,),
                    User(nickname = "prueba02",nombre = "pruebaRoom2", apellidos = "no", contrasena = "321", email = "prueba2.com", fechaNacimiento = "22/22/2222", tipoUsuario = 1, nivel = 4)
                )
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val usuarios = db.getUserDao().getAll()
            usuarios.forEach { Log.i("BBDD", it.toString()) }

        }

        // -----------------------


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