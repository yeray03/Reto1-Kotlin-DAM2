package com.example.spinningcat.model

data class Workout(
    var id: String = "",
    var nombre: String = "",
    var descripcion: String = "",
    var nivel: Int = 0,
    var videoUrl: String? = null
)