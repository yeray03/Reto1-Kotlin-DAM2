package com.example.spinningcat.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        findViewById<Button>(R.id.btnLogin_Login).setOnClickListener{
            val user: String = findViewById<EditText>(R.id.txtNameLogin).text.toString()
            val passwd: String = findViewById<EditText>(R.id.txtPasswdLogin).text.toString()
            if (user == "admin" && passwd == "1234"){
                Toast.makeText(applicationContext, "Login correcto", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(applicationContext,"Login incorrecto",Toast.LENGTH_SHORT).show()
        }



    }
}