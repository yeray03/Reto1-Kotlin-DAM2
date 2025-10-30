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
    var tiempoTotal: String = "",
    var tiempoPrevisto: String = "",
    var fecha: String = "",
    var porcentajeCompletado: Int = 0,
    var videoUrl: String = ""
) : Serializable