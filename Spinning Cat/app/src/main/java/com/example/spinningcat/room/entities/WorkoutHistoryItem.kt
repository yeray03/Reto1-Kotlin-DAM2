package com.example.spinningcat.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Entidad para el histórico de workouts realizados por el usuario.
 */
@Entity(tableName = "t_workout_history")
data class WorkoutHistoryItem(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var nombre: String = "",
    var nivel: Int = 0,
    var tiempoTotal: Long = 0,
    var tiempoPrevisto: Long = 0,
    var fecha: String = "",
    var porcentajeCompletado: Int = 0,
    var videoUrl: String = ""
) : Serializable