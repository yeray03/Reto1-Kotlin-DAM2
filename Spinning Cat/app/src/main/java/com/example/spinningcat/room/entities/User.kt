package com.example.spinningcat.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "t_usuario")

data class User(
    @PrimaryKey(autoGenerate = false)
    var nickname: String = "",
    var nombre: String = "",
    var apellidos: String = "",
    var contrasena: String = "",
    var email: String = "",
    var fechaNacimiento: String = "",
    var tipoUsuario: Int = -1,
    var nivel: Int = -1,
) : Serializable