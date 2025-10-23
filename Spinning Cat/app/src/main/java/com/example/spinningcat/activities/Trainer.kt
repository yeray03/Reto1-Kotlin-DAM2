package com.example.spinningcat.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.example.spinningcat.adapter.TrainerWorkoutAdapter
import com.example.spinningcat.model.Workout
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.text.set
import kotlin.toString

class Trainer : AppCompatActivity() {

    private var adapter: TrainerWorkoutAdapter? = null
    private var workouts: MutableList<Workout> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer)

        // Referencias a vistas
        val recyclerWorkouts = findViewById<RecyclerView>(R.id.recyclerWorkouts)
        val btnAddWorkout = findViewById<Button>(R.id.btnFilter2)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val editTextNumber = findViewById<EditText>(R.id.editTextNumber)
        val btnFilter = findViewById<Button>(R.id.btnFiltrar)

        // Adapter
        adapter = TrainerWorkoutAdapter(
            workouts,
            onModificar = { workout -> modificarWorkout(workout) },
            onEliminar = { workout -> eliminarWorkout(workout) },
            onReproducir = { url -> reproducirVideo(url) }
        )
        recyclerWorkouts.layoutManager = LinearLayoutManager(this)
        recyclerWorkouts.adapter = adapter

        // Cargar workouts de Firestore al iniciar
        cargarWorkouts()

        // Añadir workout
        btnAddWorkout.setOnClickListener {
            // Ejemplo de añadir uno rápido
            val nuevo = Workout(
                id = System.currentTimeMillis().toString(),
                nombre = "Workout molón",
                descripcion = "Cardio al fallo",
                nivel = 0,
                videoUrl = null
            )
            guardarWorkoutEnFirestore(nuevo)
        }

        // Filtrar workouts, por nivel fijo (1)
   /*     btnFilter.setOnClickListener {
            val filtro = editTextFilter.text.toString().trim()
            if (filtro.isEmpty()) {
                adapter?.setWorkouts(workouts)
            } else {
                val filtrados = workouts.filter {
                    it.nombre.contains(filtro, ignoreCase = true) ||
                            it.nivel.toString() == filtro
                }
                adapter?.setWorkouts(filtrados)
            }
        }*/
        btnFilter.setOnClickListener {
            val nivelStr = findViewById<EditText>(R.id.editTextNumber).text.toString().trim()
            val nivel = nivelStr.toIntOrNull()
            if (nivel != null) {
                val filtrados = workouts.filter { it.nivel == nivel }
                adapter?.setWorkouts(filtrados)
            } else {
                adapter?.setWorkouts(workouts)
                Toast.makeText(this, "Introduce un nivel válido", Toast.LENGTH_SHORT).show()
            }
        }


        // Volver
        btnCancel.setOnClickListener {
            finish()
        }
    }

    // Carga todos los workouts desde Firestore
    private fun cargarWorkouts() {
        val db = FirebaseFirestore.getInstance()
        db.collection("workouts").get().addOnSuccessListener { result ->
            workouts.clear()
            for (doc in result) {
                val workout = doc.toObject(Workout::class.java)
                // Asegúrate de que el id se guarda también
                workout.id = doc.id
                workouts.add(workout)
            }
            adapter?.setWorkouts(workouts)
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar workouts", Toast.LENGTH_SHORT).show()
        }
    }

    // Guardar un nuevo workout en Firestore
    private fun guardarWorkoutEnFirestore(workout: Workout) {
        val db = FirebaseFirestore.getInstance()
        db.collection("workouts").document(workout.id).set(workout)
            .addOnSuccessListener {
                cargarWorkouts() // Recarga la lista completa desde Firestore
                Toast.makeText(this, "Workout añadido", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al añadir workout", Toast.LENGTH_SHORT).show()
            }
    }


    // Modificar un workout (debes implementar un diálogo para editarlo y luego actualizar Firestore)
    private fun modificarWorkout(workout: Workout) {
        // Aquí deberías mostrar un diálogo de edición
        // Por simplicidad, se cambia solo el nombre (puedes expandirlo)
        workout.nombre = "Modificado"
        val db = FirebaseFirestore.getInstance()
        db.collection("workouts").document(workout.id).set(workout)
            .addOnSuccessListener {
                adapter?.updateWorkout(workout)
                Toast.makeText(this, "Workout modificado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al modificar", Toast.LENGTH_SHORT).show()
            }
    }

    // Eliminar un workout
    private fun eliminarWorkout(workout: Workout) {
        val db = FirebaseFirestore.getInstance()
        db.collection("workouts").document(workout.id).delete()
            .addOnSuccessListener {
                workouts.remove(workout)
                adapter?.removeWorkout(workout)
                Toast.makeText(this, "Workout eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
    }

    // Reproducir vídeo
    private fun reproducirVideo(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}