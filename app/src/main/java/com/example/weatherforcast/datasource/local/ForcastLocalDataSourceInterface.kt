package com.example.weatherforcast.datasource.local

import com.example.weatherforcast.model.Response.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface ForcastLocalDataSourceInterface {
    suspend fun insertForcastItem(forcast: ForecastResponse)

    suspend fun deleteForcastItem(forcast: ForecastResponse)

    fun getAllStoredForecasts(): Flow<List<ForecastResponse>>
}