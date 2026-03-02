package com.example.weatherforcast.datasource.local

import androidx.room.*
import com.example.weatherforcast.model.AlertItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertsDao {
    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): Flow<List<AlertItem>> // Flow allows real-time updates

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertItem)

    @Delete
    suspend fun deleteAlert(alert: AlertItem)
}