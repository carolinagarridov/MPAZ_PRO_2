package com.example.mpaz_pro_2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mpaz_pro_2.UserEntity
import com.example.mpaz_pro_2.ProgressEntity
import com.example.mpaz_pro_2.UserDao
@Database(entities = [UserEntity::class, ProgressEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpaz_database"
                ).build().also { INSTANCE = it }
            }
    }
}