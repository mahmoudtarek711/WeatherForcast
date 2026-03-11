package com.example.weatherforcast.viewmodels

import android.location.Geocoder
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.data.local.UserSettings
import com.example.weatherforcast.model.*
import com.example.weatherforcast.utils.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SettingsViewModel(
    private val settingsManager: SettingsManager,
    private val locationProvider: LocationProvider,
    private val context: android.content.Context
) : ViewModel() {

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


    fun updateLocationMode(mode: LocationMode) {
        viewModelScope.launch { settingsManager.saveLocationMode(mode) }
    }

    fun updateCity(city: String) {
        viewModelScope.launch { settingsManager.saveCity(city) }
    }
    fun updateLanguage(lang: Language) {
        // 1. Save to DataStore so the app knows the user's preference
        viewModelScope.launch { settingsManager.saveLanguage(lang) }

        // 2. Apply the system locale
        val code = if (lang == Language.AR) "ar" else "en"
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
    fun updateToCurrentLocation() {
        viewModelScope.launch {
            // Use withContext(Dispatchers.IO) to ensure Geocoder doesn't block the UI
            val cityName = withContext(Dispatchers.IO) {
                val location = locationProvider.getCurrentLocation()
                location?.let { (lat, lon) ->
                    val geocoder = Geocoder(context, Locale.getDefault())
                    // Use the older getFromLocation as it is more reliable for 1 result
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    addresses?.firstOrNull()?.locality
                }
            }

            // Now back on Main (or the scope's dispatcher) to update state
            cityName?.let {
                updateCity(it)
                updateLocationMode(LocationMode.GPS)
            }
        }
    }
}