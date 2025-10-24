package com.example.spinningcat.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.spinningcat.room.entities.RememberedUser

@Dao
interface RememberDao {
    @Query("select * from t_remember order by nickname")
    fun getRemember(): List<RememberedUser>

    @Insert
    fun insertRemember(vararg usuario: RememberedUser)
    @Query("DELETE FROM t_remember")
    fun clearRememberedUser()
}