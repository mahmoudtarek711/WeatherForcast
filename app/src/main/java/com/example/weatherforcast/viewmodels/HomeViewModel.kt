package com.example.weatherforcast.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.model.Response.ForecastResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ForcastRepository,
    private val settingsManager: SettingsManager
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
            // Create a flow that handles its own retries internally
            flow {
                while (true) { // Infinite loop for retrying
                    try {
                        repository.getRemoteForecast(46.23, 2.21, "76c0ba629d316a5c11c0ead182aefac9")
                            .collect { response ->
                                if (wasInError) {
                                    _errorEvents.emit("Internet back! Updating weather...")
                                    wasInError = false
                                }
                                emit(response)
                            }
                        break // Success! Break the while loop
                    } catch (e: Exception) {
                        wasInError = true
                        _errorEvents.emit("No Internet Connection. Retrying in 10s...")
                        emit(null)
                        delay(10000) // Wait 10 seconds before trying the 'while' loop again
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}