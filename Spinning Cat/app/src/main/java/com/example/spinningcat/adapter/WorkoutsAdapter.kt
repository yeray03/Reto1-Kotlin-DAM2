package com.example.spinningcat.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.example.spinningcat.room.entities.WorkoutHistoryItem

// Adaptador solo de lectura, para mostrar el histórico de workouts.
 class WorkoutsAdapter(private var workouts: List<WorkoutHistoryItem>) :
    RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.nombreText)
        val nivel: TextView = view.findViewById(R.id.nivelText)
        val tiempoTotal: TextView = view.findViewById(R.id.tiempoTotalText)
        val tiempoPrevisto: TextView = view.findViewById(R.id.tiempoPrevistoText)
        val fecha: TextView = view.findViewById(R.id.fechayHoraText)
        val porcentaje: TextView = view.findViewById(R.id.ejerciciosTotales)
        val videoButton: Button = view.findViewById(R.id.videoButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_history, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val item = workouts[position]
        holder.nombre.text = item.nombre
        holder.nivel.text = "Nivel: ${item.nivel}"
        holder.tiempoTotal.text = "Tiempo total: ${item.tiempoTotal}"
        holder.tiempoPrevisto.text = "Tiempo previsto: ${item.tiempoPrevisto}"
        holder.fecha.text = "Fecha: ${item.fecha}"
        holder.porcentaje.text = "% Completados: ${item.porcentajeCompletado}"
        holder.videoButton.setOnClickListener {
            if (item.videoUrl.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.videoUrl))
                holder.itemView.context.startActivity(intent)
            } else {
                Toast.makeText(holder.itemView.context, "No hay vídeo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = workouts.size

    // Metodo para actualizar la lista filtrada
    fun actualizarLista(nuevaLista: List<WorkoutHistoryItem>) {
        workouts = nuevaLista
        notifyDataSetChanged()
    }

}