package com.example.weatherforcast.ui.components.homecomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.ui.theme.GlassStroke
import com.example.weatherforcast.ui.theme.GlassWhite
import com.example.weatherforcast.ui.theme.GreyLight
import com.example.weatherforcast.ui.theme.RainTeal
import com.example.weatherforcast.ui.theme.TextSizes
import com.example.weatherforcast.ui.theme.TextWhite

@Composable
fun InsightsCard(type:String="Cloud", value:String="42",icon: androidx.compose.ui.graphics.vector.ImageVector,modifier: Modifier = Modifier)
{
    Column(
        modifier = modifier.padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(

            shape = RoundedCornerShape(16.dp),
            // This is the magic part:
            colors = CardDefaults.cardColors(
                containerColor = GlassWhite // Your semi-transparent color
            ),
            // Adding a thin border makes it look like real glass
            border = BorderStroke(1.dp, GlassStroke)
        ){
            Icon(
                imageVector = icon,
                contentDescription = "Location icon",
                tint = RainTeal,
                modifier = Modifier.size(40.dp).align(Alignment.CenterHorizontally).padding(8.dp)
            )
        }
        Text(text = value, fontSize = TextSizes.medium, color = TextWhite)

        Text(text = "$type", fontSize = TextSizes.small, color = GreyLight)
    }
}
