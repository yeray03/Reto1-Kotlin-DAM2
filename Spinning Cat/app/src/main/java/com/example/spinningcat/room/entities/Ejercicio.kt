package com.example.spinningcat.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.spinningcat.room.converter.Converters


@Entity(tableName = "t_ejercicio")
@TypeConverters(Converters::class)
data class Ejercicio(
    val descripcion : String = "",
    @PrimaryKey(autoGenerate = false)
    val nombre : String = "",
    val series : List<Serie> = emptyList(),
)