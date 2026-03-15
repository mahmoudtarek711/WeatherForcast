package com.example.weatherforcast.ui.components.homecomponents

import android.text.Layout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherforcast.R
import com.example.weatherforcast.ui.theme.GlassStroke
import com.example.weatherforcast.ui.theme.GlassWhite
import com.example.weatherforcast.ui.theme.GlassWhiteLight
import com.example.weatherforcast.ui.theme.RainTeal
import com.example.weatherforcast.ui.theme.TextSizes
import com.example.weatherforcast.ui.theme.TextWhite


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DayInsights(
    temprature: String = "22",
    desc: String = "Partly Cloudy",
    feels_like: String = "20",
    humidity: String = "0",
    wind: String = "0",
    pressure: String = "0",
    clouds: String = "0",
    iconUrl: String = "") {
    Card(
        modifier = Modifier
            .fillMaxWidth().fillMaxHeight() // Changed from fillMaxSize to fit nicely in a list
            .padding(16.dp).padding(bottom = 20.dp),
        shape = RoundedCornerShape(28.dp),
        // This is the magic part:
        colors = CardDefaults.cardColors(
            containerColor = GlassWhite // Your semi-transparent color
        ),
        // Adding a thin border makes it look like real glass
        border = BorderStroke(1.dp, GlassStroke)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = iconUrl,
                contentDescription = "Weather Icon",
                modifier = Modifier.size(100.dp) // Adjust size as needed
            )
            Text(text = temprature, fontSize = TextSizes.xxxLarge, color = TextWhite)
            Text(text = desc, fontSize = TextSizes.xLarge, color = TextWhite)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications, // You may need to import Icons.Default.LocationOn
                    contentDescription = "Location icon",
                    tint = RainTeal,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(8.dp)) // Adds space between icon and text

                Text(
                    text = stringResource(R.string.feels_like) +" "+ feels_like,
                    fontSize = TextSizes.large,
                    color = RainTeal
                )

            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = GlassStroke // Use your glass border color for consistency
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                InsightsCard(
                    type = stringResource(R.string.humidity),
                    value = humidity,
                    icon = Icons.Default.WaterDrop, // Humidity Icon
                    modifier = Modifier.weight(1f)
                )
                InsightsCard(
                    type = stringResource(R.string.wind),
                    value = wind,
                    icon = Icons.Default.Air, // Wind Icon
                    modifier = Modifier.weight(1f)
                )
                InsightsCard(
                    type = stringResource(R.string.pressure),
                    value = pressure,
                    icon = Icons.Default.Compress, // Pressure Icon
                    modifier = Modifier.weight(1f)
                )
                InsightsCard(
                    type = stringResource(R.string.clouds),
                    value = clouds,
                    icon = Icons.Default.Cloud, // Clouds Icon
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
