package com.example.weatherforcast.ui.components.homecomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.R
import com.example.weatherforcast.model.uiutils.HourWeather
import com.example.weatherforcast.ui.theme.TextSizes
import com.example.weatherforcast.ui.theme.TextWhite

@Composable
fun HourlyForecast(hours: List<HourWeather>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {

        Text(
            text = stringResource(R.string.today),
            color = TextWhite,
            fontSize = TextSizes.large,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            items(hours.size) { index ->

                val hour = hours[index]
                val iconUrl = "https://openweathermap.org/img/wn/${hour.iconCode}@2x.png"
                HourCard(
                    time = hour.time,
                    degree = hour.degree,
                    iconUrl = iconUrl
                )
            }
        }
    }
}