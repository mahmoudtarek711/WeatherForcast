package com.example.architechturestartercode.data.movie.repository

import com.example.architechturestartercode.data.movie.datasource.local.ForcastLocalDataSource
import com.example.architechturestartercode.data.movie.datasource.remote.ForcastRemoteDataSource
import com.example.weatherforcast.datasource.local.AlertsDao
import com.example.weatherforcast.model.AlertItem
import com.example.weatherforcast.model.Response.ForecastItem
import com.example.weatherforcast.model.Response.ForecastResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ForcastRepository(
    private val remoteDataSource: ForcastRemoteDataSource,
    private val localDataSource: ForcastLocalDataSource,
    private val alertsDao: AlertsDao
) {
    // Fetch from Remote
    fun getRemoteForecast(lat: Double, lon: Double, apiKey: String): Flow<ForecastResponse> =
        flow{
            try {
                // 1. Ask the Remote Data Source for the data (one-shot call)
                val response: ForecastResponse = remoteDataSource.getAllMovies(lat, lon, apiKey)

                // 2. "emit" means pushing the data into the pipe
                emit(response)
            } catch (e: Exception) {
                // If something breaks (like no internet), we can handle it here
                throw e
            }
        }

    // Local DB Operations
    suspend fun saveForecastItem(item: ForecastItem) {
        localDataSource.insertForcastItem(item)
    }

    suspend fun deleteForecastItem(item: ForecastItem) {
        localDataSource.deleteForcastItem(item)
    }

    //alert
    fun getAllAlerts() = alertsDao.getAllAlerts()

    suspend fun deleteAlert(alert: AlertItem) = alertsDao.deleteAlert(alert)

    // Inside ForcastRepository class
    suspend fun saveAlert(alert: AlertItem): Long {
        return alertsDao.insertAlert(alert) // Change: Return the result of the insert
    }

    suspend fun deleteAlertById(alertId: Long) {
        alertsDao.deleteAlertById(alertId)
    }
}