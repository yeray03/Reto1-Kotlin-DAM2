package com.example.spinningcat.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.example.spinningcat.activities.EjercicioDetail
import com.example.spinningcat.room.entities.WorkoutHistoryItem

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

        // ✅ Click en el item abre el detalle
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, EjercicioDetail::class.java)
            intent.putExtra("WORKOUT_NOMBRE", item.nombre)
            intent.putExtra("WORKOUT_NIVEL", item.nivel)
            intent.putExtra("WORKOUT_TIEMPO_TOTAL", item.tiempoTotal)
            intent.putExtra("WORKOUT_TIEMPO_PREVISTO", item.tiempoPrevisto)
            intent.putExtra("WORKOUT_FECHA", item.fecha)
            intent.putExtra("WORKOUT_PORCENTAJE", item.porcentajeCompletado)
            intent.putExtra("WORKOUT_VIDEO_URL", item.videoUrl)
            holder.itemView.context.startActivity(intent)
        }

        // ✅ El botón ahora dice "Ver Detalle"
        holder.videoButton.text = "Ver Detalle"
        holder.videoButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, EjercicioDetail::class.java)
            intent.putExtra("WORKOUT_NOMBRE", item.nombre)
            intent.putExtra("WORKOUT_NIVEL", item.nivel)
            intent.putExtra("WORKOUT_TIEMPO_TOTAL", item.tiempoTotal)
            intent.putExtra("WORKOUT_TIEMPO_PREVISTO", item.tiempoPrevisto)
            intent.putExtra("WORKOUT_FECHA", item.fecha)
            intent.putExtra("WORKOUT_PORCENTAJE", item.porcentajeCompletado)
            intent.putExtra("WORKOUT_VIDEO_URL", item.videoUrl)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = workouts.size

    fun actualizarLista(nuevaLista: List<WorkoutHistoryItem>) {
        workouts = nuevaLista
        notifyDataSetChanged()
    }
}