package com.example.currencyconverterpro.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, FavoritePair::class], version = 2, exportSchema = false) // <-- NAIKKAN VERSI DATABASE KE 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "currency_converter_database"
                )
                    .fallbackToDestructiveMigration() //  TAMBAHKAN BARIS INI
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}