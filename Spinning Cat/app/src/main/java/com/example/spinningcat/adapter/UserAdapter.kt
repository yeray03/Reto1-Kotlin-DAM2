package com.example.spinningcat.adapter

data class UserAdapter(
    var id: String = "",
    var nombre: String = "",
    var apellidos: String = "",
    var contrasena: String = "",
    var email: String = "",
    var fechaNacimiento: String = "",
    var tipoUsuario: Int = -1,
    var nivel: Int = -1,
    var historico: String = ""
)
