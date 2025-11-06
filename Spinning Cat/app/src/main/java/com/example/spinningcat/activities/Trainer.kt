package com.example.spinningcat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.example.spinningcat.adapter.TrainerWorkoutAdapter
import com.example.spinningcat.room.entities.Workout
import com.example.spinningcat.room.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference

class Trainer : AppCompatActivity() {

    private var usuario: User = User()
    private var adapter: TrainerWorkoutAdapter? = null
    private var workouts: MutableList<Workout> = mutableListOf()
    private var workoutsFiltrados: MutableList<Workout> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer)

        // Referencias a vistas
        val recyclerWorkouts = findViewById<RecyclerView>(R.id.recyclerWorkouts)
        val btnAddWorkout = findViewById<Button>(R.id.btnFilter2)
        val btnCancel = findViewById<Button>(R.id.btnGoback)
        val spinnerNivel = findViewById<Spinner>(R.id.spinnerNivel)
        val imageViewProfile = findViewById<ImageView>(R.id.imageViewProfile)

        // Recibir el usuario del intent
        val extras: Bundle? = intent.extras
        @Suppress("DEPRECATION")
        if (extras?.getSerializable("usuario") != null) {
            usuario = extras.getSerializable("usuario") as User
            Log.i("Trainer", "Usuario recibido: ${usuario.nombre}")
        }

        // Adapter con la lista filtrada
        adapter = TrainerWorkoutAdapter(
            workoutsFiltrados,
            onModificar = { workout -> modificarWorkout(workout) },
            onEliminar = { workout -> eliminarWorkout(workout) },
        )
        recyclerWorkouts.layoutManager = LinearLayoutManager(this)
        recyclerWorkouts.adapter = adapter

        // Cargar workouts de Firestore al iniciar
        cargarWorkouts()

        // Boton para añadir workout
        btnAddWorkout.setOnClickListener {
            val dialog = AñadirWorkoutDialog(this) { nuevoWorkout ->
                cargarWorkouts()
            }
            dialog.show()
        }

        // Ir a perfil de usuario
        imageViewProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        // Rellenar el spinner con niveles del usuario
        val niveles: ArrayList<String> = arrayListOf("Todos")
        for (i in 0..(usuario.nivel)) {
            niveles.add(i.toString())
        }
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNivel.adapter = adapterSpinner

        // Listener del spinner para filtrar por nivel
        spinnerNivel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val seleccion = niveles[position]

                if (seleccion == "Todos") {
                    // Mostrar todos los workouts
                    workoutsFiltrados.clear()
                    workoutsFiltrados.addAll(workouts)
                    adapter?.notifyDataSetChanged()
                } else {
                    // Filtrar por nivel
                    val nivelSeleccionado = seleccion.toIntOrNull()
                    if (nivelSeleccionado != null) {
                        filtrarPorNivel(nivelSeleccionado)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }

        // Volver a Login
        btnCancel.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Carga todos los workouts desde Firestore
    private fun cargarWorkouts() {
        val db = FirebaseFirestore.getInstance()
        db.collection("workouts").get().addOnSuccessListener { result ->
            workouts.clear()
            for (doc in result) {
                val data = doc.data
                val id = doc.id
                val nombre = data["nombre"] as? String ?: ""
                val nivel = (data["nivel"] as? Long)?.toInt() ?: 0
                val numEjercicio = (data["numEjercicio"] as? Long)?.toInt() ?: 0
                val videoUrl = data["videoUrl"] as? String ?: ""

                // Buscar el campo correcto: ejercicios
                val ejerciciosRefs = data["ejercicios"] as? List<*> ?: emptyList<Any>()
                val ejerciciosDocumentRefs = ejerciciosRefs.mapNotNull { ref ->
                    if (ref is DocumentReference) {
                        ref
                    } else {
                        null
                    }
                }

                if (nivel <= usuario.nivel) {
                    val workout = Workout(
                        id = id,
                        nombre = nombre,
                        nivel = nivel,
                        ejercicios = ejerciciosDocumentRefs,
                        numEjercicio = numEjercicio,
                        videoUrl = videoUrl
                    )
                    workouts.add(workout)
                }
            }

            // Actualizar la lista filtrada con todos los workouts inicialmente
            workoutsFiltrados.clear()
            workoutsFiltrados.addAll(workouts)
            adapter?.notifyDataSetChanged()

        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar workouts", Toast.LENGTH_SHORT).show()
        }
    }

    // Filtrar workouts por nivel
    private fun filtrarPorNivel(nivel: Int) {
        workoutsFiltrados.clear()
        workoutsFiltrados.addAll(workouts.filter { it.nivel == nivel })
        adapter?.notifyDataSetChanged()

        if (workoutsFiltrados.isEmpty()) {
            Toast.makeText(this, "No hay workouts de nivel $nivel", Toast.LENGTH_SHORT).show()
        }
    }

    // Modificar un workout
    private fun modificarWorkout(workout: Workout) {
        val dialog = ModificarWorkoutDialog(this, workout) { workoutModificado ->
            cargarWorkouts()
        }
        dialog.show()
    }

    // Eliminar un workout
    private fun eliminarWorkout(workout: Workout) {
        val db = FirebaseFirestore.getInstance()
        if (workout.id.isEmpty()) {
            Toast.makeText(this, "Error: ID de workout no válido", Toast.LENGTH_SHORT).show()
            return
        }
        db.collection("workouts").document(workout.id).delete()
            .addOnSuccessListener {
                cargarWorkouts()
                Toast.makeText(this, "Workout eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
    }

}