package com.example.spinningcat.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.example.spinningcat.adapter.EjerciciosAdapter
import com.example.spinningcat.room.entities.Ejercicio
import com.example.spinningcat.room.entities.Serie
import com.google.firebase.firestore.FirebaseFirestore

class EjercicioDetail : AppCompatActivity() {

    private lateinit var tvWorkoutNombre: TextView
    private lateinit var tvNivel: TextView
    private lateinit var tvTiempoTotal: TextView
    private lateinit var tvTiempoPrevisto: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvPorcentaje: TextView
    private lateinit var rvEjercicios: RecyclerView
    private lateinit var btnReproducirVideo: Button
    private lateinit var btnVolver: Button

    private lateinit var ejerciciosAdapter: EjerciciosAdapter
    private val ejerciciosList = mutableListOf<Ejercicio>()
    private val db = FirebaseFirestore.getInstance()

    private var videoUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_detail)

        initViews()
        setupRecyclerView()
        loadWorkoutData()
        setupListeners()
    }

    private fun initViews() {
        tvWorkoutNombre = findViewById(R.id.tvWorkoutNombre)
        tvNivel = findViewById(R.id.tvNivel)
        tvTiempoTotal = findViewById(R.id.tvTiempoTotal)
        tvTiempoPrevisto = findViewById(R.id.tvTiempoPrevisto)
        tvFecha = findViewById(R.id.tvFecha)
        tvPorcentaje = findViewById(R.id.tvPorcentaje)
        rvEjercicios = findViewById(R.id.rvEjercicios)
        btnReproducirVideo = findViewById(R.id.btnReproducirVideo)
        btnVolver = findViewById(R.id.btnVolver)
    }

    private fun setupRecyclerView() {
        ejerciciosAdapter = EjerciciosAdapter(ejerciciosList)
        rvEjercicios.apply {
            layoutManager = LinearLayoutManager(this@EjercicioDetail)
            adapter = ejerciciosAdapter
        }
    }

    private fun loadWorkoutData() {
        val workoutNombre = intent.getStringExtra("WORKOUT_NOMBRE") ?: ""
        val nivel = intent.getIntExtra("WORKOUT_NIVEL", 0)
        val tiempoTotal = intent.getLongExtra("WORKOUT_TIEMPO_TOTAL", 0)
        val tiempoPrevisto = intent.getLongExtra("WORKOUT_TIEMPO_PREVISTO", 0)
        val fecha = intent.getStringExtra("WORKOUT_FECHA") ?: ""
        val porcentaje = intent.getIntExtra("WORKOUT_PORCENTAJE", 0)
        videoUrl = intent.getStringExtra("WORKOUT_VIDEO_URL") ?: ""

        tvWorkoutNombre.text = workoutNombre
        tvNivel.text = "Nivel: $nivel"
        tvTiempoTotal.text = "Tiempo Total: ${formatTime(tiempoTotal)}"
        tvTiempoPrevisto.text = "Tiempo Previsto: ${formatTime(tiempoPrevisto)}"
        tvFecha.text = if (fecha.isNotEmpty()) "Fecha: $fecha" else ""
        tvPorcentaje.text = "Completado: $porcentaje%"

        btnReproducirVideo.visibility = if (videoUrl.isNotEmpty()) View.VISIBLE else View.GONE

        loadEjercicios(workoutNombre)
    }

    private fun loadEjercicios(workoutNombre: String) {
        db.collection("workouts")
            .whereEqualTo("nombre", workoutNombre)
            .get()
            .addOnSuccessListener { workoutDocs ->
                if (workoutDocs.isEmpty) {
                    Toast.makeText(this, "No se encontró el workout", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val workoutDoc = workoutDocs.documents[0]

                // ✅ Buscar "ejerciciosTotales" (con T mayúscula)
                val ejerciciosRefs = workoutDoc.get("ejerciciosTotales") as? List<*>

                if (ejerciciosRefs.isNullOrEmpty()) {
                    Toast.makeText(this, "Este workout no tiene ejercicios", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                ejerciciosList.clear()
                var loadedCount = 0

                ejerciciosRefs.forEach { ref ->
                    val ejercicioId = when (ref) {
                        is String -> {
                            ref.substringAfterLast("/")
                        }
                        is com.google.firebase.firestore.DocumentReference -> {
                            ref.id
                        }
                        else -> null
                    }

                    if (ejercicioId != null) {
                        db.collection("ejercicios")
                            .document(ejercicioId)
                            .get()
                            .addOnSuccessListener { ejercicioDoc ->
                                if (ejercicioDoc.exists()) {
                                    val nombre = ejercicioDoc.getString("nombre") ?: ""
                                    val descripcion = ejercicioDoc.getString("descripcion") ?: ""

                                    val seriesData = ejercicioDoc.get("series") as? List<Map<String, Any>> ?: emptyList()
                                    val series = seriesData.map { serieMap ->
                                        Serie(
                                            fotoUrl = serieMap["fotoUrl"] as? String ?: "",
                                            nombre = serieMap["nombre"] as? String ?: "",
                                            repeticiones = (serieMap["repeticiones"] as? Long)?.toInt() ?: -1,
                                            tiempoDescanso = (serieMap["tiempoDescanso"] as? Long)?.toInt() ?: -1,
                                            tiempoSerie = (serieMap["tiempoSerie"] as? Long)?.toInt() ?: -1
                                        )
                                    }

                                    ejerciciosList.add(
                                        Ejercicio(
                                            descripcion = descripcion,
                                            nombre = nombre,
                                            series = series
                                        )
                                    )
                                }

                                loadedCount++
                                if (loadedCount == ejerciciosRefs.size) {
                                    ejerciciosAdapter.actualizarLista(ejerciciosList)
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al cargar ejercicio: ${e.message}", Toast.LENGTH_SHORT).show()
                                loadedCount++
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al buscar workout: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupListeners() {
        btnReproducirVideo.setOnClickListener {
            if (videoUrl.isNotEmpty()) {
                val intent = Intent(this, VideoPlayer::class.java)
                intent.putExtra("VIDEO_URL", videoUrl)
                intent.putExtra("WORKOUT_NAME", tvWorkoutNombre.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "No hay video disponible", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }


}