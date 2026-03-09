package com.example.weatherforcast.ui.components.homecomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.R
import com.example.weatherforcast.model.uiutils.DayForecast
import com.example.weatherforcast.ui.theme.GlassStroke
import com.example.weatherforcast.ui.theme.GlassWhite
import com.example.weatherforcast.ui.theme.GreyLight
import com.example.weatherforcast.ui.theme.RainTeal
import com.example.weatherforcast.ui.theme.TextSizes
import com.example.weatherforcast.ui.theme.TextWhite

@Composable
fun ForecastCard(days: List<DayForecast>) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = stringResource(R.string.seven_day_forecast),
            color = TextWhite,
            fontSize = TextSizes.large,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = GlassWhite),
            border = BorderStroke(1.dp, GlassStroke)
        ) {
            // Using Column + forEach because 7 items is small and avoids
            // nested scrolling issues inside the HomeScreen LazyColumn
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                days.forEachIndexed { index, day ->
                    ForecastRow(
                        day = day.day,
                        date = day.date,
                        status = day.status,
                        highTemp = day.highTemp,
                        lowTemp = day.lowTemp
                    )

                    if (index < days.size - 1) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = GlassStroke
                        )
                    }
                }
            }
        }
    }
}