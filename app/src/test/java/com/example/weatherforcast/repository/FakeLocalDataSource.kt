package com.example.weatherforcast.repository

import com.example.architechturestartercode.data.movie.datasource.local.ForcastLocalDataSource
import com.example.weatherforcast.datasource.local.ForcastLocalDataSourceInterface
import com.example.weatherforcast.model.Response.ForecastResponse
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLocalDataSource : ForcastLocalDataSourceInterface {
    // A local list to act as our "Database" memory
    private val memory = mutableListOf<ForecastResponse>()

    // A StateFlow to emit the list whenever it changes
    private val _forecastFlow = MutableStateFlow<List<ForecastResponse>>(emptyList())

    override suspend fun insertForcastItem(forcast: ForecastResponse) {
        memory.add(forcast)
        _forecastFlow.value = memory.toList() // Update the flow with a copy of the list
    }

    override suspend fun deleteForcastItem(forcast: ForecastResponse) {
        memory.remove(forcast)
        _forecastFlow.value = memory.toList()
    }

    override fun getAllStoredForecasts(): Flow<List<ForecastResponse>> {
        return _forecastFlow
    }
}