package com.example.spinningcat.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_remember")
data class RememberedUser(
    @PrimaryKey(autoGenerate = false)
    var nickname: String = "",
    var contrasena: String = ""
)
