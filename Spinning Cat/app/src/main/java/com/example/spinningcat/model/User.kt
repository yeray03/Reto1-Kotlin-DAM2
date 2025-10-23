package com.example.spinningcat.model

data class User(
    var nombre: String = "",
    var apellidos: String = "",
    var contrasena: String = "",
    var email: String = "",
    var fechaNacimiento: String = "",
    var tipoUsuario: Int = -1,
    var nivel: Int = -1,
    var historico: String = ""
)