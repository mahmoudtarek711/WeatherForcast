package com.example.weatherforcast.utils

import com.example.weatherforcast.model.Response.ForecastItem
import com.example.weatherforcast.model.uiutils.DayForecast

fun extractFiveDaysForecast(list: List<ForecastItem>): List<DayForecast> {

    val grouped = list.groupBy { it.dt_txt.substring(0, 10) } // group by date

    return grouped.entries.take(5).mapIndexed { index, entry ->

        val items = entry.value

        val high = items.maxOf { it.main.temp }
        val low = items.minOf { it.main.temp }

        val weather = items[items.size / 2] // middle of the day

        val dayName = when (index) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> weather.dt_txt.substring(0,10)
        }

        DayForecast(
            day = dayName,
            date = weather.dt_txt.substring(5,10),
            status = weather.weather[0].description,
            highTemp = kelvinToCelsius(high),
            lowTemp = kelvinToCelsius(low)
        )
    }
}