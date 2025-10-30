package com.example.spinningcat.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.spinningcat.room.entities.User

@Dao
interface UsuarioDao {
    @Query("select * from t_usuario order by nickname")
    fun getAll(): List<User>

    @Insert
    fun insertAll(vararg usuario: User)



}