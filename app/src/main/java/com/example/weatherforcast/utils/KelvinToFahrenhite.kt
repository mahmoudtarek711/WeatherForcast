package com.example.weatherforcast.utils

fun kelvinToFahrenheit(kelvin: Double): Int {
    return ((kelvin - 273.15) * 9/5 + 32).toInt()
}