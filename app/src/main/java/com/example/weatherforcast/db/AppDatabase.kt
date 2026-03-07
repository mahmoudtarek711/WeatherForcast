package com.example.architechturestartercode.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.architechturestartercode.data.movie.datasource.local.ForcastDao
import com.example.weatherforcast.data.db.Converters
import com.example.weatherforcast.datasource.local.AlertsDao
import com.example.weatherforcast.model.AlertItem
import com.example.weatherforcast.model.Response.ForecastItem

@Database(entities = [ForecastItem::class, AlertItem::class], version = 2) // Increase version
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ForcastDao(): ForcastDao
    abstract fun alertsDao(): AlertsDao // Add this

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "forecast_db"
                )
                    .fallbackToDestructiveMigration() // Use this if you don't mind clearing old data
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}