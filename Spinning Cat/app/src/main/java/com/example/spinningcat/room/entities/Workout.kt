package com.example.spinningcat.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_workout")
data class Workout(
    @PrimaryKey(autoGenerate = false)
    var nombre: String = "",
    var descripcion: String = "",
    var nivel: Int = 0,
    var videoUrl: String? = null
)