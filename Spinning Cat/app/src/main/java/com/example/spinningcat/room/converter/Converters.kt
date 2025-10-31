package com.example.spinningcat.room.converter

import androidx.room.TypeConverter
import com.example.spinningcat.room.entities.Serie
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.util.ArrayList

class Converters {
    private val gson = Gson()

    // Conversor para List<String>
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return if (list == null) null else gson.toJson(list)
    }
    //
    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }

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

    // Convertir List<DocumentReference> a List<String> (paths)
    @TypeConverter
    fun fromDocumentReferenceList(refs: List<DocumentReference>?): String {
        val paths = refs?.map { it.path } ?: emptyList()
        return gson.toJson(paths)
    }

    @TypeConverter
    fun toDocumentReferenceList(value: String): List<DocumentReference> {
        val listType = object : TypeToken<List<String>>() {}.type
        val paths: List<String> = gson.fromJson(value, listType) ?: emptyList()
        val db = FirebaseFirestore.getInstance()
        return paths.map { path -> db.document(path) }
    }
}