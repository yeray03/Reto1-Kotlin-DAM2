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
import com.example.spinningcat.room.entities.WorkoutHistoryItem
import com.google.firebase.firestore.FirebaseFirestore

class Workouts : AppCompatActivity() {
    private var filterEditText: EditText? = null
    private var filterButton: Button? = null
    private var profileButton: Button? = null
    private var trainerButton: Button? = null
    private var backButton: Button? = null
    private var workoutsRecyclerView: RecyclerView? = null
    private lateinit var adapter: WorkoutsAdapter // No es nullable, usamos lateinit porque
    private var nickname: String = ""
    private var isTrainer = false
    private var userLevel = 1
    private val workoutsList = mutableListOf<WorkoutHistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        nickname = intent.getStringExtra("nickname") ?: ""

        val userLevelLabel = findViewById<TextView>(R.id.userLevelLabel)
        filterEditText = findViewById(R.id.filterEditText)
        filterButton = findViewById(R.id.filterButton)
        profileButton = findViewById(R.id.profileButton)
        trainerButton = findViewById(R.id.btnGoback)
        backButton = findViewById(R.id.btnGoback)
        workoutsRecyclerView = findViewById(R.id.workoutsRecyclerView)

        isTrainer = intent.getBooleanExtra("isTrainer", false)
        userLevel = intent.getIntExtra("userLevel", 1)

        userLevelLabel.text = "Nivel del usuario: $userLevel"
        trainerButton?.visibility = if (isTrainer) View.VISIBLE else View.GONE

        adapter = WorkoutsAdapter(workoutsList)
        workoutsRecyclerView?.layoutManager = LinearLayoutManager(this)
        workoutsRecyclerView?.adapter = adapter

        cargarHistorialFirestore()

        filterButton?.setOnClickListener {
            val nivel = filterEditText?.text?.toString()?.toIntOrNull()
            if (nivel != null) {
                filtrarPorNivel(nivel)
            } else {
                Toast.makeText(this, "Introduce un nivel válido", Toast.LENGTH_SHORT).show()
            }
        }

        profileButton?.setOnClickListener {
            val intent = Intent (this, Profile::class.java)
            startActivity(intent)
            Toast.makeText(this, "Ir a Perfil", Toast.LENGTH_SHORT).show()
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
        // nickname es el id de documento
        if (nickname.isBlank()) {
            Toast.makeText(this, "Error: usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }
        val dbFirestore = FirebaseFirestore.getInstance()
        dbFirestore.collection("usuarios")
            .document(nickname) // ID documento = nickname
            .collection("historico")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val workout = WorkoutHistoryItem(
                        nombre = document.getString("workoutNombre") ?: "",
                        nivel = document.getLong("nivel")?.toInt() ?: 0,
                        tiempoTotal = document.getString("tiempoTotal") ?: "",
                        tiempoPrevisto = document.getString("tiempoPrevisto") ?: "",
                        fecha = document.getString("fecha") ?: "",
                        porcentajeCompletado = document.getLong("porcentajeCompletado")?.toInt() ?: 0,
                        videoUrl = document.getString("videoUrl") ?: ""
                    )
                    workoutsList.add(workout)
                }
                workoutsList.sortByDescending { it.fecha }
                adapter.actualizarLista(workoutsList)
                if (workoutsList.isEmpty()) {
                    Toast.makeText(this, "No hay históricos", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar históricos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filtrarPorNivel(nivel: Int) {
        val filtrados = workoutsList.filter { it.nivel == nivel }
        adapter?.actualizarLista(filtrados)
    }
}

