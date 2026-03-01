package com.example.weatherforcast.utils

import com.example.weatherforcast.model.Response.ForecastItem
import com.example.weatherforcast.model.uiutils.HourWeather

fun extractTodayHours(list: List<ForecastItem>): List<HourWeather> {
    return list.take(8).map {
        HourWeather(
            time = it.dt_txt.substring(11,16),   // extract HH:mm
            degree = kelvinToCelsius(it.main.temp)
        )
    }
}