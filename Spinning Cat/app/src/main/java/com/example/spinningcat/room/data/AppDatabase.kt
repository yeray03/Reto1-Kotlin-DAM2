package com.example.spinningcat.room.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spinningcat.room.dao.UserDao
import com.example.spinningcat.room.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // Un patrón singleton para tener una única instancia de esa clase, y reutilizarla en vez de instanciarla cada vez que se necesite.
    companion object {
        @Volatile private var instance: AppDatabase? = null

        private val LOCK = Any()

        // código a ejecutar al contruirse la clase. Funciona como un semáforo. usando syncronized (LOCK) para que no puedan crearse varias instancias a la vez.
        operator fun invoke (context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase (context).also { instance = it }
        }

        private fun buildDatabase (context: Context) = Room.databaseBuilder (context,
            AppDatabase::class.java, "myDatabase").build()

    }

    abstract fun usuarioRememberDao(): UserDao
}