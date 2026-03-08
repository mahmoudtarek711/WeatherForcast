package com.example.weatherforcast.data.db

import androidx.room.TypeConverter
import com.example.weatherforcast.model.Response.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // --- ForecastResponse Fields ---
    @TypeConverter
    fun fromForecastItemList(value: List<ForecastItem>?): String = gson.toJson(value)

    @TypeConverter
    fun toForecastItemList(value: String): List<ForecastItem> {
        val type = object : TypeToken<List<ForecastItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromCity(city: City?): String = gson.toJson(city)

    @TypeConverter
    fun toCity(value: String): City = gson.fromJson(value, City::class.java)

    // --- ForecastItem Fields (Nested) ---
    @TypeConverter
    fun fromWeatherList(value: List<Weather>?): String = gson.toJson(value)

    @TypeConverter
    fun toWeatherList(value: String): List<Weather> {
        val type = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromMain(main: Main?): String = gson.toJson(main)

    @TypeConverter
    fun toMain(value: String): Main = gson.fromJson(value, Main::class.java)

    @TypeConverter
    fun fromWind(wind: Wind?): String = gson.toJson(wind)

    @TypeConverter
    fun toWind(value: String): Wind = gson.fromJson(value, Wind::class.java)

    @TypeConverter
    fun fromClouds(clouds: Clouds?): String = gson.toJson(clouds)

    @TypeConverter
    fun toClouds(value: String): Clouds = gson.fromJson(value, Clouds::class.java)

    @TypeConverter
    fun fromSys(sys: Sys?): String = gson.toJson(sys)

    @TypeConverter
    fun toSys(value: String): Sys = gson.fromJson(value, Sys::class.java)

    @TypeConverter
    fun fromRain(rain: Rain?): String? = rain?.let { gson.toJson(it) }

    @TypeConverter
    fun toRain(value: String?): Rain? = value?.let { gson.fromJson(it, Rain::class.java) }
}