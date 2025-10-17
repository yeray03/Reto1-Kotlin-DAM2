package com.example.spinningcat.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class Login : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
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