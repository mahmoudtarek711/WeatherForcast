package com.example.weatherforcast.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.data.local.UserSettings
import com.example.weatherforcast.model.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsManager: SettingsManager) : ViewModel() {

    // Observe settings from DataStore as a StateFlow
    val settingsState: StateFlow<UserSettings> = settingsManager.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings(TempUnit.C, WindUnit.MS, Language.EN, LocationMode.GPS, "Cairo")
        )

    fun updateTempUnit(unit: TempUnit) {
        viewModelScope.launch { settingsManager.saveTempUnit(unit) }
    }

    fun updateWindUnit(unit: WindUnit) {
        viewModelScope.launch { settingsManager.saveWindUnit(unit) }
    }

    fun updateLanguage(lang: Language) {
        viewModelScope.launch { settingsManager.saveLanguage(lang) }
    }

    fun updateLocationMode(mode: LocationMode) {
        viewModelScope.launch { settingsManager.saveLocationMode(mode) }
    }

    fun updateCity(city: String) {
        viewModelScope.launch { settingsManager.saveCity(city) }
    }
}