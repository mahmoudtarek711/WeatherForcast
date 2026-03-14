package com.example.weatherforcast.model.uiutils

data class DayForecast(
    val day: String,
    val date: String,
    val status: String,
    val highTemp: String,
    val lowTemp: String,
    val iconCode: String
)