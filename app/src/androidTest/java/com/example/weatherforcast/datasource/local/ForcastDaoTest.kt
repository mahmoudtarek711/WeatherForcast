package com.example.weatherforcast.datasource.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.architechturestartercode.data.db.AppDatabase
import com.example.architechturestartercode.data.movie.datasource.local.ForcastDao
import com.example.weatherforcast.model.Response.City
import com.example.weatherforcast.model.Response.Coord
import com.example.weatherforcast.model.Response.ForecastItem
import com.example.weatherforcast.model.Response.ForecastResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForcastDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: ForcastDao

    @Before
    fun setup() {
        // Create a temporary in-memory database
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Allowed only for tests
            .build()
        dao = database.ForcastDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertDay_oneForecast_storedSuccessfully() = runTest {
        // Given: A real data object (avoid mockk for DAO tests)
        val forecast: ForecastResponse = ForecastResponse(
            id = 1,
            cod = "cod",
            message = 0,
            cnt = 0,
            list = emptyList(),
            city = City(
                id = 0,
        name = "TODO()",
        coord = Coord(10.0,10.0),
        country = "TODO()",
        population = 0,
        timezone = 0,
        sunrise = 0,
        sunset = 0
        )
        )

        // When: Inserting into the DB
        dao.insertDay(forecast)

        // Then: Retrieve it and check
        val result = dao.getAllStoredForecasts().first()
        assertThat(result.size, `is`(1))
        assertThat(result[0].id, `is`(1))
    }

    @Test
    fun deleteDay_existingForecast_listBecomesEmpty() = runTest {
        // Given: An item already in the DB
        val forecast = ForecastResponse(
            id = 1,
            cod = "cod",
            message = 0,
            cnt = 0,
            list = emptyList(),
            city = City(
                id = 0,
                name = "TODO()",
                coord = Coord(10.0,10.0),
                country = "TODO()",
                population = 0,
                timezone = 0,
                sunrise = 0,
                sunset = 0
            )
        )
        dao.insertDay(forecast)

        // When: Deleting that item
        dao.deleteDay(forecast)

        // Then: The flow should emit an empty list
        val result = dao.getAllStoredForecasts().first()
        assertThat(result.isEmpty(), `is`(true))
    }


    @Test
    fun getAllStoredForecast_5Forecasts_listSizeIs5() = runTest {
        // Given: An item already in the DB
        for (i in 1..5) {
            val forecast = ForecastResponse(
                id = i,
                cod = "cod",
                message = 0,
                cnt = 0,
                list = emptyList(),
                city = City(
                    id = 0,
                    name = "TODO()",
                    coord = Coord(10.0,10.0),
                    country = "TODO()",
                    population = 0,
                    timezone = 0,
                    sunrise = 0,
                    sunset = 0
                )
            )
            dao.insertDay(forecast)
        }
        // When: request All Items List
        val result = dao.getAllStoredForecasts().first()

        // Then: The flow should emit an empty list
        assertThat(result.size, `is`(5))
    }


    @Test
    fun onConflictReplace_5Forecasts_listSizeIs1() = runTest {
        // Given: An item already in the DB
        val forecast = ForecastResponse(
            id = 1,
            cod = "cod",
            message = 0,
            cnt = 0,
            list = emptyList(),
            city = City(
                id = 0,
                name = "TODO()",
                coord = Coord(10.0,10.0),
                country = "TODO()",
                population = 0,
                timezone = 0,
                sunrise = 0,
                sunset = 0
            )
        )

        for (i in 1..5) {
            dao.insertDay(forecast)
        }
        // When: request All Items List
        val result = dao.getAllStoredForecasts().first()

        // Then: The flow should emit an empty list
        assertThat(result.size, `is`(1))
    }
}