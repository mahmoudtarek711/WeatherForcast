package com.example.weatherforcast.viewmodels

import android.content.Context
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
    private val context: Context
) : ViewModel() {

    val settingsState: StateFlow<UserSettings> = settingsManager.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings(TempUnit.C, WindUnit.MS, Language.EN, LocationMode.GPS, "Cairo")
        )

    fun updateTempUnit(unit: TempUnit) { viewModelScope.launch { settingsManager.saveTempUnit(unit) } }
    fun updateWindUnit(unit: WindUnit) { viewModelScope.launch { settingsManager.saveWindUnit(unit) } }
    fun updateLocationMode(mode: LocationMode) { viewModelScope.launch { settingsManager.saveLocationMode(mode) } }

    fun updateLanguage(lang: Language) {
        viewModelScope.launch {
            settingsManager.saveLanguage(lang)
            val code = if (lang == Language.AR) "ar" else "en"
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
        }
    }

    // Called when the user picks a location on the MapSelectionSheet
    fun updateCityFromMap(lat: Double, lon: Double) {
        viewModelScope.launch {
            val cityName = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.adminArea
                } catch (e: Exception) { null }
            }
            cityName?.let {
                settingsManager.saveManualCity(it)
                settingsManager.saveLocationMode(LocationMode.MANUAL)
            }
        }
    }

    // Called for GPS button/automatic updates
    fun updateToCurrentLocation() {
        viewModelScope.launch {
            val cityName = withContext(Dispatchers.IO) {
                val location = locationProvider.getCurrentLocation()
                location?.let { (lat, lon) ->
                    val geocoder = Geocoder(context, Locale.getDefault())
                    geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()?.locality
                }
            }
            cityName?.let {
                settingsManager.saveGpsCity(it)
                settingsManager.saveLocationMode(LocationMode.GPS)
            }
        }
    }
}