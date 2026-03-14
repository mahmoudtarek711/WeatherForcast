package com.example.weatherforcast.ui.components.homecomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.ui.theme.BlueAccent
import com.example.weatherforcast.ui.theme.Grey
import com.example.weatherforcast.ui.theme.RainTeal
import com.example.weatherforcast.ui.theme.TextLightGrey
import com.example.weatherforcast.ui.theme.TextSizes

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeHeader(city:String="Cairo" , date:String="Thursday,Feb,26")
{
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn, // You may need to import Icons.Default.LocationOn
                contentDescription = "Location icon",
                tint = RainTeal,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp)) // Adds space between icon and text

            Text(
                text = city,
                fontSize = TextSizes.xLarge,
                color = RainTeal
            )
        }
    }

        Text(date, fontSize = TextSizes.large, color = TextLightGrey)
    }
