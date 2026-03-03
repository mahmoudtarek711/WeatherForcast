package com.example.weatherforcast.datasource.local

import androidx.room.*
import com.example.weatherforcast.model.AlertItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertsDao {
    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): Flow<List<AlertItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertItem): Long // Change: Return Long

    @Delete
    suspend fun deleteAlert(alert: AlertItem)

    @Query("DELETE FROM alerts WHERE id = :alertId")
    suspend fun deleteAlertById(alertId: Long)
}