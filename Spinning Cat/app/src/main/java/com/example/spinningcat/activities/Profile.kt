package com.example.spinningcat.activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.spinningcat.R
import com.example.spinningcat.adapter.SpinnerAdapter
import com.example.spinningcat.room.entities.User
import java.util.Calendar
import java.util.Locale

class Profile : AppCompatActivity() {

    private lateinit var etLogin: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etEmail: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var btnVolver: Button
    private lateinit var btnGuardar: Button
    private lateinit var switchTema: Switch
    private lateinit var spinnerIdioma: Spinner
    private lateinit var tvNombre: TextView
    private lateinit var tvLogin: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var btnEdit: ImageButton

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // Configurar tema antes de llamar a super.onCreate usando SharedPreferences
        sharedPreferences = getSharedPreferences("GymPrefs", Context.MODE_PRIVATE)
        val temaOscuro = sharedPreferences.getBoolean("temaOscuro", false)
        AppCompatDelegate.setDefaultNightMode(
            if (temaOscuro) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Referencias a los componentes
        etLogin = findViewById(R.id.etLogin)
        etNombre = findViewById(R.id.etNombre)
        etApellidos = findViewById(R.id.etApellidos)
        etEmail = findViewById(R.id.etEmail)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        btnVolver = findViewById(R.id.btnVolver)
        btnGuardar = findViewById(R.id.btnGuardar)
        switchTema = findViewById(R.id.switchTema)
        spinnerIdioma = findViewById(R.id.spinnerIdioma)
        tvNombre = findViewById(R.id.tvNombre)
        tvLogin = findViewById(R.id.tvLogin)
        ivAvatar = findViewById(R.id.ivAvatar)
        btnEdit = findViewById(R.id.btnEdit)

        // Cargar datos del usuario (ejemplo simulado)
        val user = cargarUsuarioActivo()
        mostrarUsuario(user)

        // Bloquear edición del login
        etLogin.isEnabled = false

        btnVolver.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        etFechaNacimiento.setOnClickListener {
            mostrarDatePicker()
        }

        btnEdit.setOnClickListener {
            habilitarEdicion(true)
        }

        val idiomaMovil = Locale.getDefault().language
        val idiomas = listOf(
            R.drawable.icono_espanita_foreground,
            R.drawable.icono_inglish_pitinglish_foreground
        )
        val initialPos = if (idiomaMovil == "es") 0 else 1
        val spinner = findViewById<Spinner>(R.id.spinnerIdioma)
        spinner.adapter = SpinnerAdapter(this, idiomas)
        spinner.setSelection(initialPos)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // SharedPreferences: guardar idioma seleccionado y recrear actividad si cambia
                val prefs = getSharedPreferences("settings", MODE_PRIVATE)
                val selectedLang = if (position == 0) "es" else "en"
                if (prefs.getString("lang", "") != selectedLang) {
                    prefs.edit().putString("lang", selectedLang).apply()
                    val locale = Locale(selectedLang)
                    val config = resources.configuration
                    config.setLocale(locale)
                    resources.updateConfiguration(config, resources.displayMetrics)
                    recreate()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Cambiar tema
        switchTema.isChecked = temaOscuro
        switchTema.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("temaOscuro", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun mostrarUsuario(user: User) {
        // etLogin.setText(user.login)
        etNombre.setText(user.nombre)
        etApellidos.setText(user.apellidos)
        etEmail.setText(user.email)
        etFechaNacimiento.setText(user.fechaNacimiento)
        tvNombre.text = "${user.nombre} ${user.apellidos}"
        // tvLogin.text = "@${user.login}"
    }

    private fun cargarUsuarioActivo(): User {
        // Simulación. Debería venir de Firestore, Room, etc.
        return User(
            // login = "saddeb",
            nombre = "Sandrine",
            apellidos = "Quignaudon",
            email = "sandrine@example.com",
            fechaNacimiento = "12/05/1996"
        )
    }

    private fun habilitarEdicion(habilitar: Boolean) {
        etNombre.isEnabled = habilitar
        etApellidos.isEnabled = habilitar
        etEmail.isEnabled = habilitar
        etFechaNacimiento.isEnabled = habilitar
        btnGuardar.visibility = if (habilitar) View.VISIBLE else View.GONE
    }

    private fun guardarCambios() {
        val user = User(
            // login = etLogin.text.toString(),
            nombre = etNombre.text.toString(),
            apellidos = etApellidos.text.toString(),
            email = etEmail.text.toString(),
            fechaNacimiento = etFechaNacimiento.text.toString()
        )
        if (validarCampos(user)) {
            mostrarUsuario(user)
            habilitarEdicion(false)
            Toast.makeText(this, getString(R.string.perfil_guardado), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.error_campos), Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarCampos(user: User): Boolean {
        return user.nombre.isNotBlank() &&
                user.apellidos.isNotBlank() &&
                user.email.contains("@") &&
                user.fechaNacimiento.isNotBlank()
    }

    private fun mostrarDatePicker() {
        val c = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val fecha = "%02d/%02d/%04d".format(dayOfMonth, monthOfYear + 1, year)
                etFechaNacimiento.setText(fecha)
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
}
