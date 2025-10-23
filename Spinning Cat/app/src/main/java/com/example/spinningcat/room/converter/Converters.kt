package com.example.spinningcat.room.converter

import androidx.room.TypeConverter
import com.example.spinningcat.room.entities.Serie
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.util.ArrayList

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSerieList(series: List<Serie>?): String? {
        return if (series.isNullOrEmpty()) null else gson.toJson(series)
    }

    @TypeConverter
    fun toSerieList(value: String?): List<Serie> {
        if (value.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<Serie>>() {}.type
        return gson.fromJson(value, listType)
    }

    // Sobrecargas para ArrayList\<Serie\> (si tu entidad usa ArrayList)
    @TypeConverter
    fun fromSerieArrayList(series: ArrayList<Serie>?): String? {
        return if (series.isNullOrEmpty()) null else gson.toJson(series)
    }

    @TypeConverter
    fun toSerieArrayList(value: String?): ArrayList<Serie> {
        if (value.isNullOrEmpty()) return ArrayList()
        val listType = object : TypeToken<ArrayList<Serie>>() {}.type
        return gson.fromJson(value, listType)
    }
}