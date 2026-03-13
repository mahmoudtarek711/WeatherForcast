package com.example.architechturestartercode.data.movie.datasource.local

import android.content.Context
import com.example.architechturestartercode.data.db.AppDatabase
import com.example.weatherforcast.datasource.local.ForcastLocalDataSourceInterface
import com.example.weatherforcast.model.Response.ForecastItem
import com.example.weatherforcast.model.Response.ForecastResponse
import kotlinx.coroutines.flow.Flow

class ForcastLocalDataSource(val forcastDao: ForcastDao): ForcastLocalDataSourceInterface {

    override suspend fun insertForcastItem(forcast: ForecastResponse) {
        forcastDao.insertDay(forcast)
    }

    override suspend fun deleteForcastItem(forcast: ForecastResponse) {
        forcastDao.deleteDay(forcast)
    }

    override fun getAllStoredForecasts(): Flow<List<ForecastResponse>> {
        return forcastDao.getAllStoredForecasts()
    }

//
//    fun getAllMovies(): LiveData<List<Movie>> {
//        return forcastDao.getAllMovies()
//    }
}

