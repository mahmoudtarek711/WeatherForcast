package com.example.weatherforcast.routes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.weatherforcast.R


sealed class Screen(val route: String, val titleResId: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.today, Icons.Default.Home)
    object Favorites : Screen("favorites", R.string.favorites, Icons.Default.FavoriteBorder)
    object Alerts : Screen("alerts", R.string.alerts, Icons.Default.Notifications)
    object Settings : Screen("settings", R.string.settings, Icons.Default.Settings)
}