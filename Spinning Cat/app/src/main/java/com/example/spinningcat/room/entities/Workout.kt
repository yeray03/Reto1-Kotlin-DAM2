package com.example.spinningcat.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable
@Entity(tableName = "t_workout")
data class Workout(
    @PrimaryKey(autoGenerate = false)
    var nombre: String = "",
    var nivel: Int = 0,
    var ejerciciosCompletado: List<String> = listOf(),
    var ejerciciosTotales: List<String> = listOf(),
    var tiempoPrevisto: String = "",
    var tiempoTotal: String = "",
    var porcentajeCompletado: Int = 0,
    var numEjercicio: Int = 0,
    var videoUrl: String = ""
) : Serializable