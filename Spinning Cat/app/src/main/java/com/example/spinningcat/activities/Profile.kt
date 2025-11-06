package com.example.spinningcat.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
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
import androidx.core.content.edit
import com.example.spinningcat.MainActivity

@SuppressLint("UseSwitchCompatOrMaterialCode")
class Profile : AppCompatActivity() {

    private var usuario: User? = null
    private var edit: Boolean = false
    private lateinit var etLogin: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etEmail: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var btnVolver: Button
    private lateinit var btnGuardar: Button
    private lateinit var tema: Switch
    private lateinit var spinner: Spinner
    private lateinit var tvNombre: TextView
    private lateinit var ivAvatar: ImageView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // Configurar tema antes de llamar a super.onCreate usando SharedPreferences
        sharedPreferences = getSharedPreferences("GymPrefs", MODE_PRIVATE)
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
        btnGuardar = findViewById(R.id.btnGuardar_Edit)
        tema = findViewById(R.id.switchTema)
        spinner = findViewById(R.id.spinnerIdioma)
        tvNombre = findViewById(R.id.tvNombre)
        ivAvatar = findViewById(R.id.ivAvatar)

        // Cargar datos del usuario (ejemplo simulado)
        val extras: Bundle? = intent.extras
        @Suppress("DEPRECATION")
        usuario = extras?.getSerializable("usuario") as User?
        mostrarUsuario(usuario)

        // Bloquear edición del login
        etLogin.isEnabled = false

        btnVolver.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            if(edit) {
                guardarCambios()
            }else{
                //cambiar a modo edicion
                habilitarEdicion(true)
            }
        }

        etFechaNacimiento.setOnClickListener {
            mostrarDatePicker()
        }

        val idiomas = listOf(
            R.drawable.icono_espanita_foreground,
            R.drawable.icono_inglish_pitinglish_foreground
        )
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val initialPos = if (prefs.getString("lang", "") == "es") 0 else 1
        spinner.adapter = SpinnerAdapter(this, idiomas)
        spinner.setSelection(initialPos)

        @Suppress("DEPRECATION")
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
                    prefs.edit { putString("lang", selectedLang) }
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
        tema.isChecked = temaOscuro
        tema.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit { putBoolean("temaOscuro", isChecked) }
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        //CERRAR SESION
        val btnCerrarSesion: Button = findViewById(R.id.btnCerrarSesion)
        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun mostrarUsuario(user: User?) {
        etLogin.setText(user?.nickname)
        etNombre.setText(user?.nombre)
        etApellidos.setText(user?.apellidos)
        etEmail.setText(user?.email)
        etFechaNacimiento.setText(user?.fechaNacimiento)
        tvNombre.text = "${user?.nombre} ${user?.apellidos}"
    }

    private fun habilitarEdicion(habilitar: Boolean) {
        etNombre.isEnabled = habilitar
        etApellidos.isEnabled = habilitar
        etEmail.isEnabled = habilitar
        etFechaNacimiento.isEnabled = habilitar
        etFechaNacimiento.isClickable = habilitar
        btnGuardar.text = getString(R.string.guardar)
        edit = true
    }

    private fun guardarCambios() {
        val user = User(
            nickname = etLogin.text.toString(),
            nombre = etNombre.text.toString(),
            apellidos = etApellidos.text.toString(),
            email = etEmail.text.toString(),
            fechaNacimiento = etFechaNacimiento.text.toString()
        )
        if (validarCampos(user)) {
            mostrarUsuario(user)
            habilitarEdicion(false)
            Toast.makeText(this, getString(R.string.perfil_guardado), Toast.LENGTH_SHORT).show()
            edit = false
            btnGuardar.text = getString(R.string.edit)
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
