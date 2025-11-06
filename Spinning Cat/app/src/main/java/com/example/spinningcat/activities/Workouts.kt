package com.example.spinningcat.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.example.spinningcat.adapter.WorkoutsAdapter
import com.example.spinningcat.room.entities.User
import com.example.spinningcat.room.entities.WorkoutHistoryItem
import com.google.firebase.firestore.FirebaseFirestore

class Workouts : AppCompatActivity() {
    private var trainerButton: Button? = null
    private var backButton: Button? = null
    private var workoutsRecyclerView: RecyclerView? = null
    private lateinit var adapter: WorkoutsAdapter
    private var nickname: String = ""
    private var isTrainer = false
    private var userLevel = 1
    private val workoutsList = mutableListOf<WorkoutHistoryItem>()
    private var usuario: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        val extras = intent.extras
        @Suppress("DEPRECATION")
        usuario = extras?.getSerializable("usuario") as? User

        nickname = usuario?.nickname ?: intent.getStringExtra("nickname") ?: ""
        userLevel = usuario?.nivel ?: intent.getIntExtra("userLevel", 1)
        isTrainer = usuario?.tipoUsuario == 1

        val spinnerNivel = findViewById<Spinner>(R.id.spinnerNivel)
        val niveles: ArrayList<String> = arrayListOf("Default")
        for (i in 0..userLevel) {
            niveles.add(i.toString())
        }
        spinnerNivel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)

        val userLevelLabel = findViewById<TextView>(R.id.userLevelLabel)
        val profile = findViewById<ImageView>(R.id.imgPerfil)
        backButton = findViewById(R.id.btnGoback)
        workoutsRecyclerView = findViewById(R.id.workoutsRecyclerView)

        userLevelLabel.text = getString(R.string.user_level_label, userLevel)
        trainerButton?.visibility = if (isTrainer) View.VISIBLE else View.GONE

        adapter = WorkoutsAdapter(workoutsList)
        workoutsRecyclerView?.layoutManager = LinearLayoutManager(this)
        workoutsRecyclerView?.adapter = adapter

        cargarHistorialFirestore()

        spinnerNivel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val nivelSeleccionado = niveles[position]
                if (nivelSeleccionado != "Default") {
                    filtrarPorNivel(nivelSeleccionado.toInt())
                } else {
                    adapter.actualizarLista(workoutsList)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        profile?.setOnClickListener {
            val intent = Intent (this, Profile::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        trainerButton?.setOnClickListener {
            startActivity(Intent(this, Trainer::class.java))
        }

        backButton?.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun cargarHistorialFirestore() {
        workoutsList.clear()

        if (nickname.isBlank()) {
            Toast.makeText(this, "Error: usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val dbFirestore = FirebaseFirestore.getInstance()
        dbFirestore.collection("usuarios")
            .document(nickname)
            .collection("historico")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "No hay históricos", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (document in result) {
                    val workoutNombre = document.getString("workoutNombre") ?: ""

                    // Lee tiempoPrevisto como número (Long)
                    val tiempoPrevisto = document.getLong("tiempoPrevisto") ?: 0L

                    val workout = WorkoutHistoryItem(
                        nombre = workoutNombre,
                        nivel = document.getLong("nivel")?.toInt() ?: 0,
                        tiempoTotal = document.getLong("tiempoTotal") ?: 0L,
                        tiempoPrevisto = tiempoPrevisto,
                        fecha = document.getString("fecha") ?: "",
                        porcentajeCompletado = document.getLong("porcentajeCompletado")?.toInt() ?: 0,
                        videoUrl = document.getString("videoUrl") ?: ""
                    )

                    workoutsList.add(workout)
                }

                workoutsList.sortByDescending { it.fecha }
                adapter.actualizarLista(workoutsList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar históricos", Toast.LENGTH_SHORT).show()
            }
    }

    // Filtrar workouts por nivel, usando la lista completa de workouts y actualizando el adapter
    private fun filtrarPorNivel(nivel: Int) {
        val filtrados = workoutsList.filter { it.nivel == nivel }
        adapter.actualizarLista(filtrados)
    }
}