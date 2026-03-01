package com.example.weatherforcast.utils

fun kelvinToCelsius(k: Double): String {
    return (k - 273.15).toInt().toString()
}