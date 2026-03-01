package com.example.architechturestartercode.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.architechturestartercode.data.movie.datasource.local.ForcastDao
import com.example.weatherforcast.data.db.Converters
import com.example.weatherforcast.model.Response.ForecastItem

@Database(entities = [ForecastItem::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moviesDao(): ForcastDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "forecast_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

