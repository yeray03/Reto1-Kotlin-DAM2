package com.example.spinningcat.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spinningcat.room.dao.EjercicioDao
import com.example.spinningcat.room.dao.RememberDao
import com.example.spinningcat.room.dao.UsuarioDao
import com.example.spinningcat.room.dao.WorkoutDao
import com.example.spinningcat.room.entities.Ejercicio
import com.example.spinningcat.room.entities.RememberedUser
import com.example.spinningcat.room.entities.User
import com.example.spinningcat.room.entities.Workout

@Database(entities = [User::class, Workout::class, Ejercicio::class, RememberedUser::class], version = 1)
abstract class RoomDB : RoomDatabase() {

    companion object {
        @Volatile
        private var instance : RoomDB? = null

        private val LOCK = Any()

        operator fun invoke (context:Context) = instance?: synchronized(LOCK){
            instance?:buildDatabase (context).also { instance = it}
        }

        private fun buildDatabase (context: Context) = Room.databaseBuilder(context,
            RoomDB::class.java,
            "myDataBase")
            .build()
    }

    // funciones para poder recoger las entidades y poder usar sus funciones internas
    abstract fun getUserDao() : UsuarioDao
    abstract fun getWorkoutDao() : WorkoutDao
    abstract fun getEjercicioDao() : EjercicioDao

    abstract fun getRememberDao() : RememberDao
}