package com.example.spinningcat.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity (

    @PrimaryKey var id: Int = 0, // Siempre 0, solo guardamos uno
    var user: String = "",
    var pass: String = "",
    var checked: Boolean = false
)