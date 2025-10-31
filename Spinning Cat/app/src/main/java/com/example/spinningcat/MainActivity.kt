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
import androidx.lifecycle.lifecycleScope
import com.example.spinningcat.activities.Register
import com.example.spinningcat.adapter.SpinnerAdapter
import com.example.spinningcat.activities.Login
import com.example.spinningcat.room.RoomDB
import com.example.spinningcat.room.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val dbFirestore = FirebaseFirestore.getInstance() //firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val db = RoomDB(this)

        // Sincronizar usuarios de Firestore y Room
        dbFirestore.collection("usuarios").get().addOnSuccessListener { result ->
            lifecycleScope.launch(Dispatchers.IO) {
                // llenar lista remota en background
                val remoteUser = mutableListOf<User>()
                for (document in result) {
                    val usuarioRemoto = document.toObject(User::class.java)
                    remoteUser.add(usuarioRemoto)
                }

               val localUser = db.getUserDao().getAll()

                // leer e insertar en Room los usuarios que no esten en local
                for (usuarioRemoto in remoteUser) {
                    // Verificar si el usuario ya existe en la base de datos local
                    val existeLocal = localUser.any { it.nickname == usuarioRemoto.nickname }
                    if (!existeLocal) {
                        // Si no existe, insertarlo en la base de datos local
                        db.getUserDao().insertAll(usuarioRemoto)
                    }
                }

                // leer e insertar en Firebase los usuarios que no esten en remoto
                for (usuario in localUser) {
                    val existeRemoto = remoteUser.any { it.nickname == usuario.nickname }
                    if (!existeRemoto) {
                        dbFirestore.collection("usuarios").document(usuario.nickname).set(usuario)
                    }
                }
            }
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


        val idiomas = listOf(
            R.drawable.icono_espanita_foreground, // posicion 0
            R.drawable.icono_inglish_pitinglish_foreground // posicion 1
        )


        val spinner = findViewById<Spinner>(R.id.idiomas)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val initialPos = if (prefs.getString("lang","") == "es") 0 else 1
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
                // accede a las preferencias de la app donde se guarda el idioma seleccionado
                val prefs = getSharedPreferences("settings", MODE_PRIVATE)
                val selectedLang = if (position == 0) "es" else "en"
                if (prefs.getString("lang", "") != selectedLang) {
                    prefs.edit { putString("lang", selectedLang) }
                    // crea un objeto Locale con el idioma seleccionado
                    val locale = Locale(selectedLang)
                    val config = resources.configuration
                    config.setLocale(locale)
                    resources.updateConfiguration(config, resources.displayMetrics)
                    // recarga la actividad para que se apliquen los cambios del idioma
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