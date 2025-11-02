package com.example.spinningcat.activities

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.spinningcat.R
import com.example.spinningcat.room.entities.Workout
import com.google.firebase.firestore.FirebaseFirestore

class AñadirWorkoutDialog(
    context: Context,
    private val onWorkoutCreado: (Workout) -> Unit
) : Dialog(context) {

    private lateinit var etNombre: EditText
    private lateinit var etNivel: EditText
    private lateinit var etVideoUrl: EditText
    private lateinit var spinnerEjercicio1: Spinner
    private lateinit var spinnerEjercicio2: Spinner
    private lateinit var spinnerEjercicio3: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private val ejerciciosDisponibles = mutableListOf<String>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_anadir_workout)

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre)
        etNivel = findViewById(R.id.etNivel)
        etVideoUrl = findViewById(R.id.etVideoUrl)
        spinnerEjercicio1 = findViewById(R.id.spinnerEjercicio1)
        spinnerEjercicio2 = findViewById(R.id.spinnerEjercicio2)
        spinnerEjercicio3 = findViewById(R.id.spinnerEjercicio3)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // Cargar ejercicios disponibles desde Firebase
        cargarEjerciciosDisponibles()

        // Botones
        btnGuardar.setOnClickListener {
            crearWorkout()
        }

        btnCancelar.setOnClickListener {
            dismiss()
        }
    }

    private fun cargarEjerciciosDisponibles() {
        db.collection("ejercicios").get()
            .addOnSuccessListener { result ->
                ejerciciosDisponibles.clear()
                for (doc in result) {
                    ejerciciosDisponibles.add(doc.id)
                }

                // Configurar spinners
                configurarSpinners()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar ejercicios", Toast.LENGTH_SHORT).show()
            }
    }

    private fun configurarSpinners() {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, ejerciciosDisponibles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerEjercicio1.adapter = adapter
        spinnerEjercicio2.adapter = adapter
        spinnerEjercicio3.adapter = adapter
    }

    private fun crearWorkout() {
        val nombre = etNombre.text.toString().trim()
        val nivelStr = etNivel.text.toString().trim()
        val videoUrl = etVideoUrl.text.toString().trim()

        // Validaciones basicas
        if (nombre.isEmpty()) {
            Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val nivel = nivelStr.toIntOrNull()
        if (nivel == null || nivel < 0) {
            Toast.makeText(context, "Nivel inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (ejerciciosDisponibles.isEmpty()) {
            Toast.makeText(context, "No hay ejercicios disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener ejercicios seleccionados
        val ejercicio1Id = spinnerEjercicio1.selectedItem as String
        val ejercicio2Id = spinnerEjercicio2.selectedItem as String
        val ejercicio3Id = spinnerEjercicio3.selectedItem as String

        // Crear lista de referencias
        val ejerciciosRefs = listOf(
            db.collection("ejercicios").document(ejercicio1Id),
            db.collection("ejercicios").document(ejercicio2Id),
            db.collection("ejercicios").document(ejercicio3Id)
        )

        // Calcular tiempo previsto
        calcularTiempoPrevisto(listOf(ejercicio1Id, ejercicio2Id, ejercicio3Id)) { tiempoPrevisto ->
            // Crear nuevo workout
            val docRef = db.collection("workouts").document()
            val nuevoWorkout = Workout(
                id = docRef.id,
                nombre = nombre,
                nivel = nivel,
                ejercicios = ejerciciosRefs,
                numEjercicio = 3,
                videoUrl = videoUrl,
                tiempoPrevisto = tiempoPrevisto,
                tiempoTotal = "",
                porcentajeCompletado = 0
            )

            // Guardar en Firebase
            docRef.set(nuevoWorkout)
                .addOnSuccessListener {
                    Toast.makeText(context, "Workout creado correctamente", Toast.LENGTH_SHORT).show()
                    onWorkoutCreado(nuevoWorkout)
                    dismiss()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error al crear workout: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun calcularTiempoPrevisto(ejerciciosIds: List<String>, callback: (String) -> Unit) {
        var tiempoTotal = 0
        var ejerciciosCargados = 0

        ejerciciosIds.forEach { ejercicioId ->
            db.collection("ejercicios").document(ejercicioId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val seriesData = doc.get("series") as? List<Map<String, Any>> ?: emptyList()

                        seriesData.forEach { serieMap ->
                            val tiempoSerie = (serieMap["tiempoSerie"] as? Long)?.toInt() ?: 0
                            val tiempoDescanso = (serieMap["tiempoDescanso"] as? Long)?.toInt() ?: 0
                            tiempoTotal += tiempoSerie + tiempoDescanso
                        }
                    }

                    ejerciciosCargados++
                    if (ejerciciosCargados == ejerciciosIds.size) {
                        // Todos los ejercicios cargados, formatear tiempo
                        callback(formatearTiempo(tiempoTotal))
                    }
                }
                .addOnFailureListener {
                    ejerciciosCargados++
                    if (ejerciciosCargados == ejerciciosIds.size) {
                        callback(formatearTiempo(tiempoTotal))
                    }
                }
        }
    }

    private fun formatearTiempo(segundos: Int): String {
        val horas = segundos / 3600
        val minutos = (segundos % 3600) / 60
        val segs = segundos % 60
        return if (horas > 0) {
            String.format("%02d:%02d:%02d", horas, minutos, segs)
        } else {
            String.format("%02d:%02d", minutos, segs)
        }
    }
}