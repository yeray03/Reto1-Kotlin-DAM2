package com.example.spinningcat.adapter

import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.example.spinningcat.activities.EjercicioDetail
import com.example.spinningcat.room.entities.Workout

class TrainerWorkoutAdapter(
    private var workouts: MutableList<Workout>,
    private val onModificar: (Workout) -> Unit,
    private val onEliminar: (Workout) -> Unit
) : RecyclerView.Adapter<TrainerWorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre = itemView.findViewById<TextView>(R.id.txtNombre)
        val txtNivel = itemView.findViewById<TextView>(R.id.txtNivel)
        val btnVerEjercicios = itemView.findViewById<Button>(R.id.btnVerEjercicios)
        val btnModificar = itemView.findViewById<Button>(R.id.btnModificar)
        val btnEliminar = itemView.findViewById<Button>(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_trainer, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.txtNombre.text = Html.fromHtml("<i>${workout.nombre.replace("_", "")}</i>")
        holder.txtNivel.text = "Nivel: ${workout.nivel}"

        // Botón "Ver Ejercicios" abre EjercicioDetail
        holder.btnVerEjercicios.setOnClickListener {
            val intent = Intent(holder.itemView.context, EjercicioDetail::class.java)
            intent.putExtra("WORKOUT_NOMBRE", workout.nombre)
            intent.putExtra("WORKOUT_NIVEL", workout.nivel)
            intent.putExtra("WORKOUT_TIEMPO_TOTAL", parseTiempo(workout.tiempoTotal))
            intent.putExtra("WORKOUT_TIEMPO_PREVISTO", workout.tiempoPrevisto) // ✅ Pasar como String
            intent.putExtra("WORKOUT_FECHA", "")
            intent.putExtra("WORKOUT_PORCENTAJE", workout.porcentajeCompletado)
            intent.putExtra("WORKOUT_VIDEO_URL", workout.videoUrl)
            holder.itemView.context.startActivity(intent)
        }

        holder.btnModificar.setOnClickListener { onModificar(workout) }
        holder.btnEliminar.setOnClickListener { onEliminar(workout) }
    }

    override fun getItemCount(): Int = workouts.size

    // Función auxiliar para convertir String a Long (segundos)
    private fun parseTiempo(tiempo: String): Long {
        if (tiempo.isEmpty()) return 0L

        return try {
            val parts = tiempo.split(":")
            when (parts.size) {
                3 -> { // HH:MM:SS
                    val hours = parts[0].toLongOrNull() ?: 0L
                    val minutes = parts[1].toLongOrNull() ?: 0L
                    val seconds = parts[2].toLongOrNull() ?: 0L
                    hours * 3600 + minutes * 60 + seconds
                }
                2 -> { // MM:SS
                    val minutes = parts[0].toLongOrNull() ?: 0L
                    val seconds = parts[1].toLongOrNull() ?: 0L
                    minutes * 60 + seconds
                }
                else -> 0L
            }
        } catch (e: Exception) {
            0L
        }
    }

    fun setWorkouts(newList: List<Workout>) {
        workouts.clear()
        workouts.addAll(newList)
        notifyDataSetChanged()
    }

    fun addWorkout(workout: Workout) {
        workouts.add(workout)
        notifyItemInserted(workouts.size - 1)
    }

    fun removeWorkout(workout: Workout) {
        val index = workouts.indexOfFirst { it.id == workout.id }
        if (index != -1) {
            workouts.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateWorkout(updated: Workout) {
        val index = workouts.indexOfFirst { it.nombre == updated.nombre }
        if (index >= 0) {
            workouts[index] = updated
            notifyItemChanged(index)
        }
    }
}