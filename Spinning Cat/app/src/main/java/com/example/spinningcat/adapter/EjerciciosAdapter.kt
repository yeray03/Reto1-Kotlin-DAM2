package com.example.spinningcat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spinningcat.R
import com.example.spinningcat.room.entities.Ejercicio

class EjerciciosAdapter(
    private var ejercicios: List<Ejercicio>
) : RecyclerView.Adapter<EjerciciosAdapter.EjercicioViewHolder>() {

    class EjercicioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvEjercicioNombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tvEjercicioDescripcion)
        val tvSeries: TextView = view.findViewById(R.id.tvEjercicioSeries)
        val llSeriesContainer: LinearLayout = view.findViewById(R.id.llSeriesContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EjercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ejercicio, parent, false)
        return EjercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EjercicioViewHolder, position: Int) {
        val ejercicio = ejercicios[position]

        holder.tvNombre.text = "${position + 1}. ${ejercicio.nombre}"
        holder.tvDescripcion.text = ejercicio.descripcion
        holder.tvSeries.text = "Series: ${ejercicio.series.size}"

        holder.llSeriesContainer.removeAllViews()

        ejercicio.series.forEachIndexed { index, serie ->
            val serieView = LayoutInflater.from(holder.itemView.context)
                .inflate(android.R.layout.simple_list_item_1, holder.llSeriesContainer, false) as TextView

            serieView.text = "  Serie ${index + 1}: ${serie.repeticiones} reps - Descanso: ${serie.tiempoDescanso}s"
            serieView.textSize = 12f
            serieView.setPadding(16, 4, 16, 4)

            holder.llSeriesContainer.addView(serieView)
        }
    }

    override fun getItemCount(): Int = ejercicios.size

    fun actualizarLista(nuevaLista: List<Ejercicio>) {
        ejercicios = nuevaLista
        notifyDataSetChanged()
    }
}