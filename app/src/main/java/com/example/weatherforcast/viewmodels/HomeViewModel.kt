package com.example.weatherforcast.ui.viewmodels

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.model.LocationMode
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.utils.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: ForcastRepository,
    private val settingsManager: SettingsManager,
    private val locationProvider: LocationProvider,
    private val context: Context
) : ViewModel() {

    sealed class LocationRequestState {
        object Idle : LocationRequestState()
        object RequestPermission : LocationRequestState()
        object OpenLocationSettings : LocationRequestState()
    }

    private val _locationRequestState = MutableStateFlow<LocationRequestState>(LocationRequestState.Idle)
    val locationRequestState = _locationRequestState.asStateFlow()

    private val _refreshTrigger = MutableStateFlow(0)

    // 1. Settings state
    val settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // 2. Forecast state is now derived from settings + refresh trigger
    val forecastState: StateFlow<ForecastResponse?> = combine(
        settingsManager.settingsFlow,
        _refreshTrigger
    ) { settings, _ -> settings }
        .filterNotNull()
        .flatMapLatest { userSettings ->
            fetchForecast(userSettings)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun fetchForecast(userSettings: com.example.weatherforcast.data.local.UserSettings): Flow<ForecastResponse?> = flow {
        val coords = if (userSettings.locationMode == LocationMode.GPS) {
            checkAndGetLocation() // Only hits GPS hardware if mode is GPS
        } else {
            getCoordsFromCityName(userSettings.selectedCity) // Uses the saved manual string
        }

        repository.getRemoteForecast(
            coords.first,
            coords.second,
            "76c0ba629d316a5c11c0ead182aefac9",
            userSettings.language.name.lowercase()
        ).collect { emit(it) }
    }

    private suspend fun checkAndGetLocation(): Pair<Double, Double> {
        return if (!locationProvider.hasLocationPermission()) {
            _locationRequestState.emit(LocationRequestState.RequestPermission)
            (30.0444 to 31.2357) // Default
        } else if (!locationProvider.isLocationEnabled()) {
            _locationRequestState.emit(LocationRequestState.OpenLocationSettings)
            (30.0444 to 31.2357) // Default
        } else {
            locationProvider.getCurrentLocation() ?: (30.0444 to 31.2357)
        }
    }

    private suspend fun getCoordsFromCityName(cityName: String): Pair<Double, Double> = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            val address = geocoder.getFromLocationName(cityName, 1)?.getOrNull(0)
            address?.let { it.latitude to it.longitude } ?: (30.0444 to 31.2357)
        } catch (e: Exception) {
            30.0444 to 31.2357
        }
    }

    fun triggerRefresh() {
        _refreshTrigger.value += 1
    }

    fun resetLocationState() {
        _locationRequestState.value = LocationRequestState.Idle
    }
}