package com.example.weatherforcast.utils

import android.content.Context

fun loadJsonFromAssets(context: Context): String {
    return context.assets.open("Fivedays3hrs.json")
        .bufferedReader()
        .use { it.readText() }
}