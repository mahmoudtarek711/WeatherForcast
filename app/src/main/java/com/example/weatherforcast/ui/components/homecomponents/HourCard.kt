package com.example.weatherforcast.ui.components.homecomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.ui.theme.GlassStroke
import com.example.weatherforcast.ui.theme.GlassWhite
import com.example.weatherforcast.ui.theme.SunYellow
import com.example.weatherforcast.ui.theme.TextSizes
import com.example.weatherforcast.ui.theme.TextWhite

@Composable
fun HourCard(
    time: String = "08:00",
    degree: String = "24",
    // You can pass an ImageVector to make it dynamic later
) {
    Card(
        modifier = Modifier
            .padding(end = 12.dp) // Space between cards in the row
            .width(70.dp), // Fixed width looks best for hourly items
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        border = BorderStroke(1.dp, GlassStroke)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = time, fontSize = TextSizes.small, color = TextWhite)

            Spacer(modifier = Modifier.height(8.dp))

            Icon(
                imageVector = Icons.Default.WbSunny, // You'll need material-icons-extended for this
                contentDescription = null,
                tint = SunYellow,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "$degree", fontSize = TextSizes.medium, color = TextWhite)
        }
    }
}