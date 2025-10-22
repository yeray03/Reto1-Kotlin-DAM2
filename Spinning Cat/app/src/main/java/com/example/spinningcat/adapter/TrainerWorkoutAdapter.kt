package com.example.spinningcat.adapter

/*
    Clase adaptadora para gestionar los workouts de los entrenadores.
    Aquí se implementan los métodos necesarios para añadir, editar,
    eliminar y listar los workouts en la interfaz de usuario.
 */
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.example.spinningcat.model.Workout

class TrainerWorkoutAdapter(
    private var workouts: MutableList<Workout>,
    private val onModificar: (Workout) -> Unit,
    private val onEliminar: (Workout) -> Unit,
    private val onReproducir: (String) -> Unit
) : RecyclerView.Adapter<TrainerWorkoutAdapter.WorkoutViewHolder>() {

    // Clase ViewHolder normal (no inner)
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre = itemView.findViewById<TextView>(R.id.txtNombre)
        val txtDescripcion = itemView.findViewById<TextView>(R.id.txtDescripcion)
        val txtNivel = itemView.findViewById<TextView>(R.id.txtNivel)
        val txtVideo = itemView.findViewById<TextView>(R.id.txtVideo)
        val imgVideo = itemView.findViewById<ImageView>(R.id.imgVideo)
        val btnModificar = itemView.findViewById<Button>(R.id.btnModificar)
        val btnEliminar = itemView.findViewById<Button>(R.id.btnEliminar)
    }
    // Métodos del adaptador
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_trainer, parent, false)
        return WorkoutViewHolder(view)
    }
    // Vincula los datos del workout a la vista
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.txtNombre.text = workout.nombre
        holder.txtDescripcion.text = workout.descripcion
        holder.txtNivel.text = "Nivel: ${workout.nivel}"

        // Mostrar vídeo si existe
        if (!workout.videoUrl.isNullOrBlank()) {
            holder.txtVideo.visibility = View.VISIBLE
            holder.imgVideo.visibility = View.VISIBLE
            holder.txtVideo.text = "Ver vídeo"
            holder.txtVideo.setOnClickListener { onReproducir(workout.videoUrl!!) }
            holder.imgVideo.setOnClickListener { onReproducir(workout.videoUrl!!) }
        } else {
            holder.txtVideo.visibility = View.GONE
            holder.imgVideo.visibility = View.GONE
        }

        holder.btnModificar.setOnClickListener { onModificar(workout) }
        holder.btnEliminar.setOnClickListener { onEliminar(workout) }
    }

    override fun getItemCount(): Int {
        return workouts.size
    }

    // Métodos para actualizar la lista
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
        val index = workouts.indexOf(workout)
        if (index >= 0) {
            workouts.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateWorkout(updated: Workout) {
        val index = workouts.indexOfFirst { it.id == updated.id }
        if (index >= 0) {
            workouts[index] = updated
            notifyItemChanged(index)
        }
    }
}