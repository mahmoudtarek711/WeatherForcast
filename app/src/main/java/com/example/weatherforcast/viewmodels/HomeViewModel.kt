package com.example.weatherforcast.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.model.Response.ForecastResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ForcastRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    // 1. Expose the settings so the UI can decide which units to show
    val settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // 2. Automatically re-fetch weather when City, Mode, or Language changes
    val forecastState: StateFlow<ForecastResponse?> = settingsManager.settingsFlow
        .flatMapLatest { userSettings ->
            // You can add logic here to use GPS or the Manual City
            val city = if (userSettings.locationMode == com.example.weatherforcast.model.LocationMode.GPS)
                "lat=46.23&lon=2.21" else userSettings.selectedCity

            flow {
                try {
                    // Update your repository to accept language if needed
                    val result = repository.getRemoteForecast(46.23, 2.21, "76c0ba629d316a5c11c0ead182aefac9")
                    emit(result)
                } catch (e: Exception) {
                    emit(null)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}