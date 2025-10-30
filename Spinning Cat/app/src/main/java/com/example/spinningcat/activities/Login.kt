package com.example.spinningcat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.spinningcat.MainActivity
import com.example.spinningcat.R
import com.example.spinningcat.room.RoomDB
import com.example.spinningcat.room.entities.RememberedUser
import com.example.spinningcat.room.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {
    private val dbFirestore = FirebaseFirestore.getInstance() //firestore instance
    private val userList = mutableListOf<User>()
    private var rememberList = listOf<RememberedUser>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val dbRoom = RoomDB(this)
        val txtUser = findViewById<EditText>(R.id.txtNameLogin)
        val txtPass = findViewById<EditText>(R.id.txtPasswdLogin)
        val chkRemember = findViewById<CheckBox>(R.id.rememberMe)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener todos los usuarios de la colección "usuarios"
        dbFirestore.collection("usuarios").get().addOnSuccessListener { result ->
            for (document in result) {
                val user =
                    document.toObject(User::class.java) // Convertir el documento a un objeto User
                userList.add(user) // Agregar el usuario a la lista
            }
            lifecycleScope.launch(Dispatchers.IO) {
                val usuarios = dbRoom.getUserDao().getAll()

                for (usuarioRemoto in userList) {
                    // Verificar si el usuario ya existe en la base de datos local
                    val existeLocalmente = usuarios.any { it.nickname == usuarioRemoto.nickname }
                    if (!existeLocalmente) {
                        // Si no existe, insertarlo en la base de datos local
                        dbRoom.getUserDao().insertAll(usuarioRemoto)
                    }
                }
                usuarios.forEach {
                    Log.i("BBDD", it.toString())
                }
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

        lifecycleScope.launch(Dispatchers.IO) {
            rememberList = dbRoom.getRememberDao().getRemember()
            if (rememberList.isNotEmpty()) {
                val usuario = rememberList.first()
                withContext(Dispatchers.Main) {
                    chkRemember.isChecked = true
                    txtUser.setText(usuario.nickname)
                    txtPass.setText(usuario.contrasena)
                }
            }
        }

        findViewById<Button>(R.id.btnLogin_Login).setOnClickListener {
            var existe = false
            val userInput: String = txtUser.text.toString()
            val passwd: String = txtPass.text.toString()

            if (userInput.isBlank() || passwd.isBlank()) { // campo usuario vacío
                Toast.makeText(applicationContext, getString(R.string.fill), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Verificar el login
            for (user in userList) { // recorrer la lista de usuarios obtenida de Firestore
                if (
                    (userInput.equals(user.email, ignoreCase = true) || userInput.equals(user.nickname, ignoreCase = true))
                    && passwd == user.contrasena
                ) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.welcome, user.nombre),
                        Toast.LENGTH_SHORT
                    ).show()

                    // Guardar o limpiar datos de RememberMe usando Room
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            if (chkRemember.isChecked) {
                                if (rememberList.isNotEmpty()) {
                                    dbRoom.getRememberDao().clearRememberedUser()
                                }
                                dbRoom.getRememberDao().insertRemember(
                                    RememberedUser(
                                        nickname = user.nickname,
                                        contrasena = user.contrasena
                                    )
                                )
                            } else {
                                if (rememberList.isNotEmpty()) {
                                    dbRoom.getRememberDao().clearRememberedUser()
                                }
                            }
                        }
                    }

                    if (user.tipoUsuario == 0) { //trainee
                        val intent = Intent(applicationContext, Trainee::class.java)
                        cerrarMainActivity()
                        startActivity(intent)
                        finish()
                        return@setOnClickListener
                    } else if (user.tipoUsuario == 1) { //trainer
                        val intent = Intent(applicationContext, Trainer::class.java)
                        cerrarMainActivity()
                        startActivity(intent)
                        finish()
                        return@setOnClickListener
                    }
                }
                if (userInput.equals(user.nickname, ignoreCase = true) || userInput.equals(user.email, ignoreCase = true)) {
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