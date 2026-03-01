package com.example.weatherforcast.model.Response
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)