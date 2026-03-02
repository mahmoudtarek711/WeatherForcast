package com.example.weatherforcast.ui.components.homecomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.ui.theme.GreyLight
import com.example.weatherforcast.ui.theme.RainTeal
import com.example.weatherforcast.ui.theme.TextSizes
import com.example.weatherforcast.ui.theme.TextWhite

@Composable
fun ForecastRow(
    day: String,
    date: String,
    status: String,
    highTemp: String,
    lowTemp: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Date Section
        Column(modifier = Modifier.width(100.dp)) {
            Text(text = day, fontSize = TextSizes.medium, color = TextWhite)
            Text(text = date, fontSize = TextSizes.small, color = GreyLight)
        }

        // 2. Icon and Description
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Cloud, // Use dynamic icons based on status
                contentDescription = null,
                tint = RainTeal,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = status, fontSize = TextSizes.small, color = TextWhite)
        }

        // 3. Temperature Range
        Row {
            Text(text = "$highTemp", fontSize = TextSizes.medium, color = TextWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "$lowTemp", fontSize = TextSizes.medium, color = GreyLight)
        }
    }
}