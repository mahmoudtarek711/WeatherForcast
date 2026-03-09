package com.example.weatherforcast.ui.viewmodels

import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.model.LocationMode
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.utils.LocationProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class HomeViewModel(
    private val repository: ForcastRepository,
    private val settingsManager: SettingsManager,
    private val locationProvider: LocationProvider,
    private val context: android.content.Context
) : ViewModel() {

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    // Keep track if we were previously in an error state
    private var wasInError = false

    val settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val forecastState: StateFlow<ForecastResponse?> = settingsManager.settingsFlow
        .flatMapLatest { userSettings ->
            flow {
                while (true) {
                    try {
                        // Determine the coordinates based on the user's settings
                        val coords = if (userSettings.locationMode == LocationMode.GPS) {
                            locationProvider.getCurrentLocation() ?: (30.0444 to 31.2357) // Default Cairo if null
                        } else {
                            getCoordsFromCityName(userSettings.selectedCity)
                        }

                        repository.getRemoteForecast(coords.first, coords.second, "76c0ba629d316a5c11c0ead182aefac9")
                            .collect { response ->
                                if (wasInError) {
                                    _errorEvents.emit("Internet back! Updating weather...")
                                    wasInError = false
                                }
                                emit(response)
                            }
                        break
                    } catch (e: Exception) {
                        // ... existing retry/error logic ...
                    }
                }
            }
        }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null)
    private fun getCoordsFromCityName(cityName: String): Pair<Double, Double> {
        return try {
            val geocoder = android.location.Geocoder(context)
            val address = geocoder.getFromLocationName(cityName, 1)?.get(0)
            if (address != null) address.latitude to address.longitude else 30.0444 to 31.2357
        } catch (e: Exception) {
            30.0444 to 31.2357
        }
    }

}