package com.example.weatherforcast.datasource.local

import com.example.architechturestartercode.data.movie.datasource.local.ForcastDao
import com.example.architechturestartercode.data.movie.datasource.local.ForcastLocalDataSource
import com.example.weatherforcast.model.Response.ForecastResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ForcastLocalDataSourceTest {


    lateinit var mockDao: ForcastDao
    lateinit var localdataSource: ForcastLocalDataSource
    @Before
    fun setup()
    {
        // 1. Create a Mock of the DAO
         mockDao = mockk()

        // 2. Create the DataSource using the mock
        localdataSource = ForcastLocalDataSource(mockDao)
    }

    @Test
    fun insertForecast_1item_callsDaoInsert() = runTest{

        //given
        val item: ForecastResponse = mockk()
        coEvery { mockDao.insertDay(item) } returns Unit

        //then
        localdataSource.insertForcastItem(item)

        //when
        coVerify { mockDao.insertDay(item) }
    }

    @Test
    fun deleteForecast_1item_callsDaoDeleteDay() = runTest{

        //given
        val item: ForecastResponse = mockk()
        coEvery { mockDao.deleteDay(item) } returns Unit
        coEvery { mockDao.insertDay(item) } returns Unit

        //then
        localdataSource.deleteForcastItem(item)

        //when
        coVerify { mockDao.deleteDay(item) }
    }

    @Test
    fun getAllForecast_callsDaoGetAllStoredfForcast() = runTest{

        //given
        val item: ForecastResponse = mockk()
        val list: Flow<List<ForecastResponse>> = mockk()

        coEvery { mockDao.getAllStoredForecasts() } returns list
        coEvery { mockDao.insertDay(item) } returns Unit
        coEvery { mockDao.deleteDay(item) } returns Unit

        //then
        localdataSource.getAllStoredForecasts()

        //when
        coVerify { mockDao.getAllStoredForecasts() }
    }

}