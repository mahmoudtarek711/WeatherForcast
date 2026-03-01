package com.example.architechturestartercode.data.movie.datasource.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforcast.model.Response.ForecastItem

@Dao
interface ForcastDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDay(day: ForecastItem)

    @Delete
    suspend fun deleteDay(day: ForecastItem)

//    @Query("SELECT * FROM movies")
//    fun getAllMovies(): LiveData<List<Movie>>
}

