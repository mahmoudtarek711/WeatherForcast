package com.example.architechturestartercode.data.movie.datasource.local

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.architechturestartercode.data.db.AppDatabase
import com.example.weatherforcast.model.Response.ForecastItem

class ForcastLocalDataSource(context: Context) {
    private val forcastDao: ForcastDao = AppDatabase.getInstance(context).moviesDao()

    suspend fun insertForcastItem(day: ForecastItem) {
        forcastDao.insertDay(day)
    }

    suspend fun deleteForcastItem(day: ForecastItem) {
        forcastDao.deleteDay(day)
    }
//
//    fun getAllMovies(): LiveData<List<Movie>> {
//        return forcastDao.getAllMovies()
//    }
}

