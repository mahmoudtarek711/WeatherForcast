package com.example.weatherforcast.network

import com.example.architechturestartercode.data.movie.datasource.remote.ForcastService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Network {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val forcastService: ForcastService by lazy {
        retrofit.create(ForcastService::class.java)
    }
}

