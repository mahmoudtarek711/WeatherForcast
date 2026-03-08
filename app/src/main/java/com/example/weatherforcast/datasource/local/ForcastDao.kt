package com.example.architechturestartercode.data.movie.datasource.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforcast.model.Response.ForecastItem
import com.example.weatherforcast.model.Response.ForecastResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface ForcastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(forcast: ForecastResponse)

    @Delete
    suspend fun deleteDay(forcast: ForecastResponse)

    @Query("SELECT * FROM forecast_table") // Replace with your actual table name from ForecastResponse entity
    fun getAllStoredForecasts(): Flow<List<ForecastResponse>>

//    @Query("SELECT * FROM movies")
//    fun getAllMovies(): LiveData<List<Movie>>
}

