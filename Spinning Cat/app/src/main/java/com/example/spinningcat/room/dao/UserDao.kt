package com.example.spinningcat.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.spinningcat.room.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 0")
    suspend fun getRememberedUser(): UserEntity?
    /**
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clearRememberedUser()

    */
}