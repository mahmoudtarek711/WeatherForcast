package com.example.architechturestartercode.data.movie.repository

import com.example.architechturestartercode.data.movie.datasource.local.ForcastLocalDataSource
import com.example.architechturestartercode.data.movie.datasource.remote.ForcastRemoteDataSource
import com.example.weatherforcast.datasource.local.AlertsDao
import com.example.weatherforcast.model.AlertItem
import com.example.weatherforcast.model.Response.ForecastItem
import com.example.weatherforcast.model.Response.ForecastResponse

class ForcastRepository(
    private val remoteDataSource: ForcastRemoteDataSource,
    private val localDataSource: ForcastLocalDataSource,
    private val alertsDao: AlertsDao
) {
    // Fetch from Remote
    suspend fun getRemoteForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse {
        return remoteDataSource.getAllMovies(lat, lon, apiKey) // Renaming this function in RemoteDS is advised
    }

    // Local DB Operations
    suspend fun saveForecastItem(item: ForecastItem) {
        localDataSource.insertMovie(item)
    }

    suspend fun deleteForecastItem(item: ForecastItem) {
        localDataSource.deleteMovie(item)
    }

    //alert
    fun getAllAlerts() = alertsDao.getAllAlerts()

    suspend fun saveAlert(alert: AlertItem) = alertsDao.insertAlert(alert)

    suspend fun deleteAlert(alert: AlertItem) = alertsDao.deleteAlert(alert)
}