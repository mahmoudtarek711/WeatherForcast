package com.example.weatherforcast.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.R
import com.example.weatherforcast.data.local.UserSettings
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.model.TempUnit
import com.example.weatherforcast.model.WindUnit
import com.example.weatherforcast.ui.components.homecomponents.DayInsights
import com.example.weatherforcast.ui.components.homecomponents.ForecastCard
import com.example.weatherforcast.ui.components.homecomponents.HomeHeader
import com.example.weatherforcast.ui.components.homecomponents.HourlyForecast
import com.example.weatherforcast.ui.theme.BlueAccent
import com.example.weatherforcast.ui.theme.BlueDark
import com.example.weatherforcast.ui.theme.BlueSecondary
import com.example.weatherforcast.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    forecast: ForecastResponse,
    settings: UserSettings,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(forecast.city.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueDark, titleContentColor = androidx.compose.ui.graphics.Color.White)
            )
        }
    ) { scaffoldpadding ->
        // Directly implementing the logic from your HomeScreen
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxSize().background(
                Brush.horizontalGradient(
                    listOf(
                        BlueSecondary,
                        BlueAccent
                    )
                )
            ).padding(scaffoldpadding)
        ) {
            item { HomeHeader(forecast.city.name) }

            item {
                val weather = forecast.list[0]
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
                    "${(weather.wind.speed * 2.237).toInt()}"+stringResource(R.string.mph)
                } else {
                    "${weather.wind.speed}"+stringResource(R.string.ms)
                }

                val humidity = "${weather.main.humidity}%"
                val pressure = "${weather.main.pressure}"+stringResource(R.string.hpa)
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

            item {
                Text(stringResource(R.string.hourly_rate), color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.padding(16.dp))
                HourlyForecast(extractTodayHours(forecast.list, settings))
            }

            item {
                ForecastCard(extractFiveDaysForecast(forecast.list, settings))
            }
        }
    }
}