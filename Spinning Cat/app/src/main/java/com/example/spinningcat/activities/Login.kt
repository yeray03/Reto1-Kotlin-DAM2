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

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnLogin_Login).setOnClickListener {
            val user: String = findViewById<EditText>(R.id.txtNameLogin).text.toString()
            val passwd: String = findViewById<EditText>(R.id.txtPasswdLogin).text.toString()
            if (user == "admin" && passwd == "1234") {
                Toast.makeText(applicationContext, "Login correcto", Toast.LENGTH_SHORT).show()

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
            } else Toast.makeText(applicationContext, "Login incorrecto", Toast.LENGTH_SHORT).show()
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