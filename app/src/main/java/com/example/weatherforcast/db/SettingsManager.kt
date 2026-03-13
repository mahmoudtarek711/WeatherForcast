package com.example.weatherforcast.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherforcast.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val TEMP_UNIT = stringPreferencesKey("temp_unit")
        private val WIND_UNIT = stringPreferencesKey("wind_unit")
        private val LANGUAGE = stringPreferencesKey("language")
        private val LOCATION_MODE = stringPreferencesKey("location_mode")
        private val SELECTED_CITY = stringPreferencesKey("selected_city") // GPS slot
        private val MANUAL_CITY = stringPreferencesKey("manual_city")     // Map selection slot
    }

    val settingsFlow: Flow<UserSettings> = dataStore.data.map { pref ->
        val mode = LocationMode.valueOf(pref[LOCATION_MODE] ?: LocationMode.GPS.name)
        UserSettings(
            tempUnit = TempUnit.valueOf(pref[TEMP_UNIT] ?: TempUnit.C.name),
            windUnit = WindUnit.valueOf(pref[WIND_UNIT] ?: WindUnit.MS.name),
            language = Language.valueOf(pref[LANGUAGE] ?: Language.EN.name),
            locationMode = mode,
            // Functional Logic: Return the correct city based on mode
            selectedCity = if (mode == LocationMode.MANUAL) {
                pref[MANUAL_CITY] ?: "Cairo"
            } else {
                pref[SELECTED_CITY] ?: "Cairo"
            }
        )
    }

    suspend fun saveTempUnit(unit: TempUnit) { dataStore.edit { it[TEMP_UNIT] = unit.name } }
    suspend fun saveWindUnit(unit: WindUnit) { dataStore.edit { it[WIND_UNIT] = unit.name } }
    suspend fun saveLanguage(lang: Language) { dataStore.edit { it[LANGUAGE] = lang.name } }
    suspend fun saveLocationMode(mode: LocationMode) { dataStore.edit { it[LOCATION_MODE] = mode.name } }

    // Specifically for GPS updates
    suspend fun saveGpsCity(city: String) {
        dataStore.edit { it[SELECTED_CITY] = city }
    }

    // Specifically for Manual Map selection
    suspend fun saveManualCity(city: String) {
        dataStore.edit { it[MANUAL_CITY] = city }
    }
}

// Data class to hold the settings state
data class UserSettings(
    val tempUnit: TempUnit,
    val windUnit: WindUnit,
    val language: Language,
    val locationMode: LocationMode,
    val selectedCity: String
)