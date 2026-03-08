import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.data.local.UserSettings
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.model.TempUnit
import com.example.weatherforcast.model.WindUnit
import com.example.weatherforcast.utils.kelvinToCelsius
import com.example.weatherforcast.utils.kelvinToFahrenheit

@Composable
fun FavoriteCard(
    item: ForecastResponse,
    settings: UserSettings,
    onClick: () -> Unit
) {
    // Calculate display temperature based on settings unit
    val kelvinTemp = item.list.firstOrNull()?.main?.temp ?: 0.0
    val displayTemp = when (settings.tempUnit) {
        TempUnit.C -> "${kelvinToCelsius(kelvinTemp)}°C"
        TempUnit.F -> "${kelvinToFahrenheit(kelvinTemp)}°F"
        TempUnit.K -> "${kelvinTemp.toInt()}K"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.city.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = item.list.firstOrNull()?.weather?.firstOrNull()?.description ?: "No Data",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Text(
                text = displayTemp,
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}