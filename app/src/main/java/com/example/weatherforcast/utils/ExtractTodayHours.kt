package com.example.weatherforcast.utils

import com.example.weatherforcast.data.local.UserSettings
import com.example.weatherforcast.model.Response.ForecastItem
import com.example.weatherforcast.model.TempUnit
import com.example.weatherforcast.model.uiutils.HourWeather

fun extractTodayHours(
    list: List<ForecastItem>,
    settings: UserSettings // Add this parameter
): List<HourWeather> {
    return list.take(8).map { item ->
        val tempKelvin = item.main.temp

        // Convert based on settings
        val formattedTemp = when (settings.tempUnit) {
            TempUnit.C -> "${kelvinToCelsius(tempKelvin)}°C" // Added ° before C
            TempUnit.F -> "${kelvinToFahrenheit(tempKelvin)}°F" // Added ° before F
            TempUnit.K -> "${tempKelvin.toInt()}K"
        }
        val iconCode = item.weather.firstOrNull()?.icon?:"01d"

        HourWeather(
            time = item.dt_txt.substring(11, 16),
            degree = formattedTemp,
            iconCode = iconCode
        )
    }
}