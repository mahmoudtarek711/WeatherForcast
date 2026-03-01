package com.example.weatherforcast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.architechturestartercode.data.movie.datasource.local.ForcastLocalDataSource
import com.example.architechturestartercode.data.movie.datasource.remote.ForcastRemoteDataSource
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.routes.Screen
import com.example.weatherforcast.ui.screens.*
import com.example.weatherforcast.ui.theme.WeatherForcastTheme
import com.example.weatherforcast.utils.loadJsonFromAssets
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val json = loadJsonFromAssets(this)

        val gson = Gson()

        val response = gson.fromJson(
            json,
            ForecastResponse::class.java
        )


        super.onCreate(savedInstanceState)
        setContent {
            WeatherForcastTheme {
                val navController = rememberNavController()
                val items = listOf(Screen.Home, Screen.Favorites, Screen.Alerts, Screen.Settings)

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            // Matching the dark blue from your gradient
                            containerColor = Color(0xFF2A4A62),
                            contentColor = Color.White
                        ) {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = null) },
                                    label = { Text(screen.title) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        unselectedIconColor = Color.Gray,
                                        indicatorColor = Color(0xFF4A7A9B) // Lighter blue for selection
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    var forecastState by remember { mutableStateOf(response) }

                    // 2. Initialize your Repository
                    val repo = ForcastRepository(
                        ForcastRemoteDataSource(),
                        ForcastLocalDataSource(this@MainActivity)
                    )

                    // 3. Launch a Coroutine to fetch live data
                    LaunchedEffect(Unit) {
                        try {
                            // Replace "" with your actual OpenWeatherMap API Key
                            val liveData = repo.getRemoteForecast(46.23, 2.21, "76c0ba629d316a5c11c0ead182aefac9")
                            forecastState = liveData // Update the UI state with live data
                        } catch (e: Exception) {
                            e.printStackTrace() // If network fails, it keeps the local JSON data
                        }
                    }
                    NavHost(
                        navController,
                        startDestination = Screen.Home.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) { HomeScreen(forecastState) }
                        composable(Screen.Favorites.route) { FavoritesScreen() }
                        composable(Screen.Alerts.route) { AlertsScreen() }
                        composable(Screen.Settings.route) { SettingsScreen() }
                    }
                }
            }
        }
    }
}