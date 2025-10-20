package com.example.spinningcat.activities

import android.content.Intent
import android.os.Bundle
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
import com.example.spinningcat.adapter.UserAdapter
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
        val userAdapterList = mutableListOf<UserAdapter>()

        // Obtener todos los usuarios de la colección "usuarios"
        db.collection("usuarios").get().addOnSuccessListener { result ->
            for (document in result) {
                val userAdapter = document.toObject(UserAdapter::class.java) // Convertir el documento a un objeto User
                //userAdapter.email = document.id // Asignar el ID del documento al campo id del usuario
                userAdapterList.add(userAdapter) // Agregar el usuario a la lista
            }
        }
            // Manejar errores al obtener los usuarios
            .addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    getString(R.string.usersError),
                    Toast.LENGTH_SHORT
                ).show()
            }


        findViewById<Button>(R.id.btnLogin_Login).setOnClickListener {
            var existe = false
            val user: String = findViewById<EditText>(R.id.txtNameLogin).text.toString()
            val passwd: String = findViewById<EditText>(R.id.txtPasswdLogin).text.toString()

            if (user.isBlank() || passwd.isBlank()) { // campo usuario vacío
                Toast.makeText(applicationContext, getString(R.string.fill), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            // Verificar el login
            for (u in userAdapterList) { // recorrer la lista de usuarios obtenidos
                if (user.equals(u.email, ignoreCase = true) && passwd == u.contrasena) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.welcome, u.nombre),
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    if (u.tipoUsuario == 0) { //trainee
                        // Ir a la actividad de Client
                        val intent = Intent(
                            applicationContext,
                            Trainee::class.java
                        )
                        cerrarMainActivity()
                        startActivity(intent)
                        finish()
                        // return de toda la vida pero con pijadas de kotlin
                        return@setOnClickListener
                    } else if (u.tipoUsuario == 1) { //trainer
                        // Ir a la actividad de Trainer
                        val intent = Intent(
                            applicationContext,
                            Trainer::class.java
                        )
                        cerrarMainActivity()
                        startActivity(intent)
                        finish()
                        return@setOnClickListener
                    }

                }
                if (user.equals(u.email, ignoreCase = true)) {
                    existe = true
                }
            }
            if (existe) { // usuario existe pero contraseña incorrecta
                Toast.makeText(applicationContext, R.string.invalidPasswd, Toast.LENGTH_SHORT)
                    .show()
            } else Toast.makeText(applicationContext, R.string.userNotFound, Toast.LENGTH_SHORT)
                .show()
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

    fun cerrarMainActivity() {
        // Cerrar la MainActivity para que no quede en la pila de actividades
        val cerrarMain = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("finish_main", true)
        }
        startActivity(cerrarMain)
    }
}