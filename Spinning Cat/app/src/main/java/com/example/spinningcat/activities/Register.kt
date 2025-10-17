package com.example.spinningcat.activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spinningcat.R


class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            fun crearCuenta() {
                TODO("Not yet implemented")
                // generar fichero para ofline y crear cuenta en firebase
            }
            finish()
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        // NOTITA PARA QUIEN LO LEA:
        // NUNCA RECREAR LA ACTIVIDAD PRINCIPAL, SOLO HACER FINISH() PARA NO REINICIAR EL IDIOMA ACTUAL
        // JUGAR CON LA PILA DE ACTIVIDADES Y LOS ARCHIVOS GENERADOS PARA LA BBDD Y LOCAL DE USUARIO
        // SOLO HACER INTENT A ACTIVIDADES QUE NO SEAN LA PRINCIPAL
        }



    }
}