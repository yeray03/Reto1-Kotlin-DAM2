package com.example.spinningcat.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spinningcat.R
import com.example.spinningcat.adapter.UserAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.system.exitProcess
import kotlin.text.set


class Register : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.alreadyAcc).setOnClickListener {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            crearCuenta()

            // generar fichero para ofline y crear cuenta en firebase


        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
            // NOTITA PARA QUIEN LO LEA:
            // NUNCA RECREAR LA ACTIVIDAD PRINCIPAL, SOLO HACER FINISH() PARA NO REINICIAR EL IDIOMA ACTUAL
            // JUGAR CON LA PILA DE ACTIVIDADES Y LOS ARCHIVOS GENERADOS PARA LA BBDD Y LOCAL DE USUARIO
            // SOLO HACER INTENT A ACTIVIDADES QUE NO SEAN LA PRINCIPAL
        }
    }

    fun crearCuenta() {
        val id = findViewById<TextView>(R.id.txtMail).text.toString()
        val nombre = findViewById<TextView>(R.id.txtName).text.toString()
        val apellidos = findViewById<TextView>(R.id.txtSurnames).text.toString()
        val contrasena = findViewById<TextView>(R.id.txtPasswd).text.toString()
        val contrasena2 = findViewById<TextView>(R.id.txtConfirmPasswd).text.toString()
        if (contrasena != contrasena2) {
            Toast.makeText(
                applicationContext,
                getString(R.string.unmatch),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val email = findViewById<TextView>(R.id.txtMail).text.toString()
        val fechaNac = findViewById<TextView>(R.id.txtDate).text.toString()
        val opcionMarcada = findViewById<RadioGroup>(R.id.Rgroup).checkedRadioButtonId
        // Validar que todos los campos estén completos
        if (id.isBlank() || nombre.isBlank() || apellidos.isBlank() ||
            contrasena.isBlank() || contrasena2.isBlank() || fechaNac.isBlank() || opcionMarcada == -1
        ) {
            Toast.makeText(
                applicationContext,
                getString(R.string.fill),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val radioUsuario = findViewById<RadioButton>(opcionMarcada).text.toString()
        var tipousuario = -1
        if (radioUsuario == "Trainee" || radioUsuario == "Cliente") { // hardcodeado porque con strings.xml solo pilla el idioma del sistema y no valida los dos idiomas
            tipousuario = 0
        } else {
            tipousuario = 1
        }
        val nivel = 0


        val usuario = UserAdapter(
            id,
            nombre,
            apellidos,
            contrasena,
            email,
            fechaNac,
            tipousuario,
            nivel
        )

        db.collection("usuarios").get()
            .addOnSuccessListener { result ->
                var existe = false
                for (document in result) {
                    val user = document.toObject(UserAdapter::class.java)
                    if (user.id.equals(usuario.id, ignoreCase = true)) {
                        existe = true
                        break
                    }
                }
                if (existe) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.userFound),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@addOnSuccessListener
                } else {
                    db.collection("usuarios").document(id).set(usuario)
                        .addOnSuccessListener {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.accountCreated),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(applicationContext, Login::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.accountCreationError),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    applicationContext,
                    getString(R.string.dbError),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
