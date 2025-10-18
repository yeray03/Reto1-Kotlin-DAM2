package com.example.spinningcat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spinningcat.MainActivity
import com.example.spinningcat.R
import com.example.spinningcat.adapter.User
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()     //firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Lista para almacenar los usuarios obtenidos de Firestore
        val userList = mutableListOf<User>()

        // Obtener todos los usuarios de la colección "usuarios"
        db.collection("usuarios").get().addOnSuccessListener { result ->
            for (document in result) {
                val user =
                    document.toObject(User::class.java) // Convertir el documento a un objeto User
                user.id = document.id // Asignar el ID del documento al campo id del usuario
                userList.add(user) // Agregar el usuario a la lista
            }
        }
            // Manejar errores al obtener los usuarios
            .addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    "Error al obtener los usuarios: $exception",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Firestore", "Error al obtener los usuarios", exception)
            }


        findViewById<Button>(R.id.btnLogin_Login).setOnClickListener {
            var existe = false
            val user: String = findViewById<EditText>(R.id.txtNameLogin).text.toString()
            val passwd: String = findViewById<EditText>(R.id.txtPasswdLogin).text.toString()

            if (user.isBlank()) { // campo usuario vacío
                Toast.makeText(applicationContext, "El usuario no existe", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            // Verificar el login
            for (u in userList) {
                if (user.equals(u.email, ignoreCase = true) && passwd == u.contraseña) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.welcome, u.nombre),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    // Cerrar la MainActivity para que no quede en la pila de actividades
                    val cerrarMain = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        putExtra("finish_main", true)
                    }
                    startActivity(cerrarMain)

                    // Ir a la actividad de Client
                    val intent = Intent(
                        applicationContext,
                        Client::class.java
                    )
                    startActivity(intent)
                    finish()
                    return@setOnClickListener // return de toda la vida pero con pijadas de kotlin
                }
                if (user.equals(u.email, ignoreCase = true)) {
                    existe = true
                }
            }
            if (existe) { // usuario existe pero contraseña incorrecta
                Toast.makeText(applicationContext, "Contraseña incorrecta", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        findViewById<TextView>(R.id.gotoRegister).setOnClickListener {
            val intent = Intent(
                applicationContext,
                Register::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}