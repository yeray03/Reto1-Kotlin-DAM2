package com.example.spinningcat.model

data class Workout(
    var nombre: String = "",
    var descripcion: String = "",
    var nivel: Int = 0,
    var videoUrl: String? = null,
    var ejercicios: List<String>? = null,
    var numEjercicios: Int = 0
)