package com.example.weatherforcast.utils

import com.example.weatherforcast.data.local.UserSettings
import com.example.weatherforcast.model.Response.ForecastItem
import com.example.weatherforcast.model.TempUnit
import com.example.weatherforcast.model.uiutils.DayForecast

fun extractFiveDaysForecast(
    list: List<ForecastItem>,
    settings: UserSettings // Add settings as a parameter
): List<DayForecast> {

    val grouped = list.groupBy { it.dt_txt.substring(0, 10) } // Group by date "YYYY-MM-DD"

    return grouped.entries.take(5).mapIndexed { index, entry ->
        val items = entry.value

        // 1. Find raw Kelvin values for the whole day
        val highKelvin = items.maxOf { it.main.temp }
        val lowKelvin = items.minOf { it.main.temp }

        // 2. Select a representative weather status for the day (mid-day)
        val representativeWeather = items[items.size / 2]

        // 3. Convert Temps based on user settings
        val formattedHigh: String
        val formattedLow: String

        when (settings.tempUnit) {
            TempUnit.C -> {
                formattedHigh = "${kelvinToCelsius(highKelvin)}°C"
                formattedLow = "${kelvinToCelsius(lowKelvin)}°C"
            }
            TempUnit.F -> {
                formattedHigh = "${kelvinToFahrenheit(highKelvin)}°F"
                formattedLow = "${kelvinToFahrenheit(lowKelvin)}°F"
            }
            TempUnit.K -> {
                formattedHigh = "${highKelvin.toInt()}°K"
                formattedLow = "${lowKelvin.toInt()}°K"
            }
        }

        val dayName = when (index) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> {
                // Optional: You can use a Date formatter here for real day names (Mon, Tue)
                representativeWeather.dt_txt.substring(0, 10)
            }
        }

        DayForecast(
            day = dayName,
            date = representativeWeather.dt_txt.substring(5, 10),
            status = representativeWeather.weather[0].description,
            highTemp = formattedHigh,
            lowTemp = formattedLow
        )
    }
}