package com.example.weatherforcast.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforcast.model.*
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.ui.components.*
import com.example.weatherforcast.ui.components.homecomponents.DayInsights
import com.example.weatherforcast.ui.components.homecomponents.ForecastCard
import com.example.weatherforcast.ui.components.homecomponents.HomeHeader
import com.example.weatherforcast.ui.components.homecomponents.HourlyForecast
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.utils.extractFiveDaysForecast
import com.example.weatherforcast.utils.extractTodayHours
import com.example.weatherforcast.utils.kelvinToCelsius


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(forcastResponse: ForecastResponse) {
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

        item {
            HomeHeader(city = forcastResponse.city.name)
        }
        item {
            val weather = forcastResponse.list[0]

            val temperature = kelvinToCelsius(weather.main.temp)
            val feelsLike = kelvinToCelsius(weather.main.feels_like)
            val description = weather.weather[0].description

            val humidity = "${weather.main.humidity}%"
            val pressure = "${weather.main.pressure} hPa"
            val wind = "${weather.wind.speed} m/s"
            val clouds = "${weather.clouds.all}%"

            DayInsights(
                temprature = temperature,
                desc = description,
                feels_like = feelsLike,
                humidity = humidity,
                wind = wind,
                pressure = pressure,
                clouds = clouds
            )
        }
        item {
            SettingsSection("Hourly Rate") {
                val todayHours = extractTodayHours(forcastResponse.list)

                HourlyForecast(todayHours)
            }
        }
        item {
            val daysForecast = extractFiveDaysForecast(forcastResponse.list)

            ForecastCard(daysForecast)
        }

    }
}
