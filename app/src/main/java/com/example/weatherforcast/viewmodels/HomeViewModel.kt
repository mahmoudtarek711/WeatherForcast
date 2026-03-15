package com.example.weatherforcast.ui.viewmodels

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.data.local.UserSettings
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

    // Using SharedFlow ensures that every call to triggerRefresh() is handled,
    // even if the value (int) is the same.
    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    // 1. Settings state
    val settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // 2. Forecast state: Reacts to Settings changes AND Manual Refresh Triggers
    val forecastState: StateFlow<ForecastResponse?> = combine(
        settingsManager.settingsFlow,
        _refreshTrigger
    ) { settings, _ -> settings }
        .flatMapLatest { userSettings ->
            fetchForecast(userSettings)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun fetchForecast(userSettings: UserSettings): Flow<ForecastResponse?> = flow {
        // Emit null first to show the loading spinner while we wait for GPS
        emit(null)

        val coords = if (userSettings.locationMode == LocationMode.GPS) {
            checkAndGetLocation()
        } else {
            getCoordsFromCityName(userSettings.selectedCity)
        }

        if (coords != null) {
            repository.getRemoteForecast(
                coords.first,
                coords.second,
                "76c0ba629d316a5c11c0ead182aefac9",
                userSettings.language.name.lowercase()
            ).collect { emit(it) }
        }
    }

    private suspend fun checkAndGetLocation(): Pair<Double, Double>? {
        return when {
            !locationProvider.hasLocationPermission() -> {
                _locationRequestState.emit(LocationRequestState.RequestPermission)
                null
            }
            !locationProvider.isLocationEnabled() -> {
                _locationRequestState.emit(LocationRequestState.OpenLocationSettings)
                null
            }
            else -> {
                // If we have permissions and GPS is ON, get the actual location
                locationProvider.getCurrentLocation()
            }
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
        viewModelScope.launch {
            _refreshTrigger.emit(Unit)
        }
    }

    fun resetLocationState() {
        _locationRequestState.value = LocationRequestState.Idle
    }
}