package com.example.weatherforcast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.architechturestartercode.data.db.AppDatabase
import com.example.architechturestartercode.data.movie.datasource.local.ForcastLocalDataSource
import com.example.architechturestartercode.data.movie.datasource.remote.ForcastRemoteDataSource
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.model.AlertItem
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.routes.Screen
import com.example.weatherforcast.ui.screens.*
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.ui.theme.WeatherForcastTheme
import com.example.weatherforcast.ui.viewmodels.AlertsViewModel
import com.example.weatherforcast.utils.loadJsonFromAssets
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.viewmodels.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherForcastTheme {
                // 1. Setup Dependencies
                val database = AppDatabase.getInstance(this)
                val settingsManager = remember { SettingsManager(this) }

                val repo = remember {
                    ForcastRepository(
                        ForcastRemoteDataSource(),
                        ForcastLocalDataSource(this),
                        database.alertsDao()
                    )
                }

                // 2. Initialize AlertsViewModel
                val alertsViewModel: AlertsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AlertsViewModel(repo) as T
                        }
                    }
                )

                // 3. Initialize SettingsViewModel
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return SettingsViewModel(settingsManager) as T
                        }
                    }
                )

                val navController = rememberNavController()
                val items = listOf(Screen.Home, Screen.Favorites, Screen.Alerts, Screen.Settings)
                var forecastState by remember { mutableStateOf<ForecastResponse?>(null) }

                LaunchedEffect(Unit) {
                    try {
                        val liveData = repo.getRemoteForecast(46.23, 2.21, "76c0ba629d316a5c11c0ead182aefac9")
                        forecastState = liveData
                    } catch (e: Exception) {
                        val json = loadJsonFromAssets(this@MainActivity)
                        forecastState = Gson().fromJson(json, ForecastResponse::class.java)
                    }
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = BlueDark, contentColor = Color.White) {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = null) },
                                    label = { Text(screen.title) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        unselectedIconColor = Color.Gray,
                                        indicatorColor = BlueSecondary
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(navController, Screen.Home.route, Modifier.padding(innerPadding)) {
                        composable(Screen.Home.route) { forecastState?.let { HomeScreen(it) } }
                        composable(Screen.Favorites.route) { FavoritesScreen() }
                        composable(Screen.Alerts.route) { AlertsScreen(viewModel = alertsViewModel) }
                        composable(Screen.Settings.route) { SettingsScreen(viewModel = settingsViewModel) }
                    }
                }
            }
        }
    }
}