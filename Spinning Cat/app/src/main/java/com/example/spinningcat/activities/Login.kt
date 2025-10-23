package com.example.spinningcat.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spinningcat.MainActivity
import com.example.spinningcat.R
import com.example.spinningcat.model.User
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    private val dbFirestore = FirebaseFirestore.getInstance() //firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val txtUser = findViewById<EditText>(R.id.txtNameLogin)
        val txtPass = findViewById<EditText>(R.id.txtPasswdLogin)
        val chkRemember = findViewById<CheckBox>(R.id.rememberMe)

      /*  lifecycleScope.launch (Dispatchers.IO) {
            val db = AppDatabase(this as Context)

            Log.d("Adapter", "Updating adapter with ${NewUserEntity.size} UserEntity") // log: muestra por consola para hacer pruebas de las tablas
            UserEntity = NewUserEntity


        }*/

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Lista para almacenar los usuarios obtenidos de Firestore
        val userList = mutableListOf<User>()

        // Obtener todos los usuarios de la colección "usuarios"
        dbFirestore.collection("usuarios").get().addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject(User::class.java) // Convertir el documento a un objeto User
                userList.add(user) // Agregar el usuario a la lista
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
            val user: String = txtUser.text.toString()
            val passwd: String = txtPass.text.toString()

            if (user.isBlank() || passwd.isBlank()) { // campo usuario vacío
                Toast.makeText(applicationContext, getString(R.string.fill), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Verificar el login
            for (u in userList) { // recorrer la lista de usuarios obtenidos
                if (user.equals(u.email, ignoreCase = true) && passwd == u.contrasena) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.welcome, u.nombre),
                        Toast.LENGTH_SHORT
                    ).show()

                    // Guardar o limpiar datos de RememberMe usando Room
                /*    lifecycleScope.launch {
                        if (chkRemember.isChecked) {
                            appDb.usuarioRememberDao().saveUser(
                                UserEntity(
                                    id = 0,
                                    user = txtUser.text.toString(),
                                    pass = txtPass.text.toString(),
                                    checked = true
                                )
                            )
                        } else {
                            appDb.usuarioRememberDao().clearRememberedUser()
                        }
                    }*/


                    if (u.tipoUsuario == 0) { //trainee
                        // Ir a la actividad de Client
                        val intent = Intent(
                            applicationContext,
                            Trainee::class.java
                        )
                        cerrarMainActivity()
                        startActivity(intent)
                        finish()
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

        // Listener para el checkbox (opcional: limpia si se desmarca)
       /* chkRemember.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                txtUser.setText("")
                txtPass.setText("")
                lifecycleScope.launch {
                    appDb.usuarioRememberDao().clearRememberedUser()
                }
            }
        }
*/
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