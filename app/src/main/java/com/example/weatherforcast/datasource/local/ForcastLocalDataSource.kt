package com.example.architechturestartercode.data.movie.datasource.local

import android.content.Context
import com.example.architechturestartercode.data.db.AppDatabase
import com.example.weatherforcast.model.Response.ForecastItem
import com.example.weatherforcast.model.Response.ForecastResponse
import kotlinx.coroutines.flow.Flow

class ForcastLocalDataSource(context: Context) {
    private val forcastDao: ForcastDao = AppDatabase.getInstance(context).ForcastDao()

    suspend fun insertForcastItem(forcast: ForecastResponse) {
        forcastDao.insertDay(forcast)
    }

    suspend fun deleteForcastItem(forcast: ForecastResponse) {
        forcastDao.deleteDay(forcast)
    }

    fun getAllStoredForecasts(): Flow<List<ForecastResponse>> {
        return forcastDao.getAllStoredForecasts()
    }

//
//    fun getAllMovies(): LiveData<List<Movie>> {
//        return forcastDao.getAllMovies()
//    }
}

