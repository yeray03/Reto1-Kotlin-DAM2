package com.example.spinningcat.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.example.spinningcat.adapter.TrainerWorkoutAdapter
import com.example.spinningcat.room.entities.Workout
import com.example.spinningcat.room.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.firestore.DocumentReference
import kotlin.text.clear
import kotlin.text.get

class Trainer : AppCompatActivity() {

    private var usuario: User = User()
    private var adapter: TrainerWorkoutAdapter? = null
    private var workouts: MutableList<Workout> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer)
        val db = FirebaseFirestore.getInstance()

        // Referencias a vistas
        val recyclerWorkouts = findViewById<RecyclerView>(R.id.recyclerWorkouts)
        val btnAddWorkout = findViewById<Button>(R.id.btnFilter2)
        val btnCancel = findViewById<Button>(R.id.btnGoback)
        val spinnerNivel = findViewById<android.widget.Spinner>(R.id.spinnerNivel)
        val imageViewProfile = findViewById<ImageView>(R.id.imageViewProfile)

        // Recibir el usuario del intent
        val extras: Bundle? = intent.extras
        @Suppress("DEPRECATION")
        if (extras?.getSerializable("usuario") != null) {
            usuario = extras.getSerializable("usuario") as User
            Log.i("Trainer", "Usuario recibido: ${usuario.nombre}")
        }

        // Adapter
        adapter = TrainerWorkoutAdapter(
            workouts, // Pasar la lista mutable vacía
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
                nombre = "WorkoutMolon",
                nivel = 1,
                ejerciciosTotales = listOf(
                    db.collection("ejercicios").document("burpees"),
                    db.collection("ejercicios").document("flexiones"),
                    db.collection("ejercicios").document("sentadilla")
                ),
                numEjercicio = 3,
                videoUrl = "https://youtu.be/R4IZ_5WxZ_g"
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
        // Ir a perfil de usuario
        imageViewProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        // Rellenar el spinner con niveles
//        val niveles = listOf("0", "1", "2", "3")
        val niveles: ArrayList<String> = arrayListOf("Default")
        for (i in 0..(usuario.nivel)) {
            niveles.add(i.toString())
        }
        val adapterSpinner =
            android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNivel.adapter = adapterSpinner

        // botón para filtrar por nivel
//        btnFilter.setOnClickListener {
//            val nivelStr = spinnerNivel.selectedItem as String
//            val nivel = nivelStr.toIntOrNull()
//            if (nivel != null) {
//                val filtrados = workouts.filter { it.nivel == nivel }
//                adapter?.setWorkouts(filtrados)
//            } else {
//                adapter?.setWorkouts(workouts)
//                Toast.makeText(this, "Selecciona un nivel válido", Toast.LENGTH_SHORT).show()
//            }
//        }

        // Volver a Login
        btnCancel.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Carga todos los workouts desde Firestore
    // obtenemos los datos como Map, convertimos el campo ejercicios a lista de String. Para evitar error de deserialización
    private fun cargarWorkouts() {
        val db = FirebaseFirestore.getInstance()
        db.collection("workouts").get().addOnSuccessListener { result ->
            val workoutsTemp =
                mutableListOf<Workout>() // lista temporal para evitar problemas de concurrencia al actualizar el adapter
            for (doc in result) {
                val data = doc.data
                val nombre = data["nombre"] as? String ?: ""
                val nivel = (data["nivel"] as? Long)?.toInt() ?: 0
                val numEjercicio = (data["numEjercicio"] as? Long)?.toInt() ?: 0
                val videoUrl = data["videoUrl"] as? String ?: ""
                val ejerciciosRefs = data["ejercicios"] as? List<*> ?: emptyList<Any>()
                val ejerciciosDocumentRefs = ejerciciosRefs.mapNotNull { ref ->
                    if (ref is DocumentReference) {
                        ref
                    } else {
                        null
                    }
                }

                val workout = Workout(
                    nombre = nombre,
                    nivel = nivel,
                    ejerciciosTotales = ejerciciosDocumentRefs,
                    numEjercicio = numEjercicio,
                    videoUrl = videoUrl
                )
                workoutsTemp.add(workout)
            }
            adapter?.setWorkouts(workoutsTemp)
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar workouts", Toast.LENGTH_SHORT).show()
        }
    }

    // Guardar un nuevo workout en Firestore
    private fun guardarWorkoutEnFirestore(workout: Workout) {
        val db = FirebaseFirestore.getInstance()
        db.collection("workouts").document().set(workout)
            .addOnSuccessListener {
                cargarWorkouts() // Recarga la lista completa desde Firestore
                Toast.makeText(this, "Workout añadido", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al añadir workout", Toast.LENGTH_SHORT).show()
            }
    }

    // Modificar un workout (aún no funcional)
    private fun modificarWorkout(workout: Workout) {
        // Aquí deberías mostrar un diálogo de edición
        // Por simplicidad, se cambia solo el nombre (puedes expandirlo)
        workout.nombre = "Modificado"
        val db = FirebaseFirestore.getInstance()
        db.collection("workouts").document(workout.nombre).set(workout)
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
        db.collection("workouts").document(workout.nombre).delete()
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