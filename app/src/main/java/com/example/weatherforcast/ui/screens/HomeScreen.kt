package com.example.weatherforcast.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherforcast.data.local.UserSettings
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.model.TempUnit
import com.example.weatherforcast.model.WindUnit
import com.example.weatherforcast.ui.components.*
import com.example.weatherforcast.ui.components.homecomponents.DayInsights
import com.example.weatherforcast.ui.components.homecomponents.ForecastCard
import com.example.weatherforcast.ui.components.homecomponents.HomeHeader
import com.example.weatherforcast.ui.components.homecomponents.HourlyForecast
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.utils.extractFiveDaysForecast
import com.example.weatherforcast.utils.extractTodayHours
import com.example.weatherforcast.utils.kelvinToCelsius
import com.example.weatherforcast.utils.kelvinToFahrenheit
import com.example.weatherforcast.R
import com.example.weatherforcast.ui.viewmodels.HomeViewModel
import com.example.weatherforcast.utils.formatDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    forcastResponse: ForecastResponse, settings: UserSettings
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(
            Brush.horizontalGradient(
                listOf(
                    BlueSecondary,
                    BlueAccent
                )
            )
        ).padding(16.dp)
    ) {

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d, h:mm a")
        val formattedDate = current.format(formatter)

        item {
            HomeHeader(city = forcastResponse.city.name , date = formattedDate)
        }
        // ... inside your LazyColumn item
        item {
            val weather = forcastResponse.list[0]
            val description = weather.weather[0].description

            val formattedTemp = when (settings.tempUnit) {
                TempUnit.C -> "${kelvinToCelsius(weather.main.temp)}°C"
                TempUnit.F -> "${kelvinToFahrenheit(weather.main.temp)}°F"
                TempUnit.K -> "${weather.main.temp.toInt()}K"
            }

            val formattedFeelsLike = when (settings.tempUnit) {
                TempUnit.C -> "${kelvinToCelsius(weather.main.feels_like)}°C"
                TempUnit.F -> "${kelvinToFahrenheit(weather.main.feels_like)}°F"
                TempUnit.K -> "${weather.main.feels_like.toInt()}K"
            }

            // Simplify this: No cast needed, and ensure it's a clean expression
            val formattedWind = if (settings.windUnit == WindUnit.MPH) {
                "${(weather.wind.speed * 2.237).toInt()} mph"
            } else {
                "${weather.wind.speed} m/s"
            }

            val humidity = "${weather.main.humidity}%"
            val pressure = "${weather.main.pressure} hPa"
            val clouds = "${weather.clouds.all}%"
            val iconCode = weather.weather[0].icon
            val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
            DayInsights(
                temprature = formattedTemp,
                feels_like = formattedFeelsLike,
                desc = description,
                humidity = humidity,
                wind = formattedWind,
                pressure = pressure,
                clouds = clouds,
                iconUrl = iconUrl
            )
        }
        // Inside HomeScreen LazyColumn
        item {
            SettingsSection(stringResource(R.string.hourly_rate)) {
                // PASS SETTINGS HERE
                val todayHours = extractTodayHours(forcastResponse.list, settings)
                HourlyForecast(todayHours)
            }
        }
        item {
            // PASS SETTINGS HERE
            val daysForecast = extractFiveDaysForecast(forcastResponse.list, settings)
            ForecastCard(daysForecast)
        }

    }
}

