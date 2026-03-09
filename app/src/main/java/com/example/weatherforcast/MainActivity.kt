package com.example.weatherforcast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.architechturestartercode.data.db.AppDatabase
import com.example.architechturestartercode.data.movie.datasource.local.ForcastLocalDataSource
import com.example.architechturestartercode.data.movie.datasource.remote.ForcastRemoteDataSource
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.routes.Screen
import com.example.weatherforcast.ui.UiState
import com.example.weatherforcast.ui.screens.*
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.ui.viewmodels.AlertsViewModel
import com.example.weatherforcast.ui.viewmodels.HomeViewModel
import com.example.weatherforcast.utils.LocationProvider
import com.example.weatherforcast.viewmodels.FavoritesViewModel
import com.example.weatherforcast.viewmodels.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val showPermissionSnackbar = mutableStateOf(false)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (!isGranted) showPermissionSnackbar.value = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNotificationPermission()

        setContent {
            WeatherForcastTheme {
                val database = remember { AppDatabase.getInstance(this) }
                val settingsManager = remember { SettingsManager(this) }
                val repo = remember { ForcastRepository(ForcastRemoteDataSource(), ForcastLocalDataSource(this), database.alertsDao()) }

                val alertsViewModel: AlertsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T = AlertsViewModel(application, repo) as T
                })
                val favoritesViewModel: FavoritesViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T = FavoritesViewModel(repo) as T
                })
                val settingsViewModel: SettingsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(settingsManager) as T
                })
                val homeViewModel: HomeViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repo, settingsManager, LocationProvider(this@MainActivity), this@MainActivity) as T
                })

                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val items = listOf(Screen.Home, Screen.Favorites, Screen.Alerts, Screen.Settings)

                if (showPermissionSnackbar.value) {
                    LaunchedEffect(snackbarHostState) {
                        val result = snackbarHostState.showSnackbar("Notifications required", "Settings", duration = SnackbarDuration.Long)
                        if (result == SnackbarResult.ActionPerformed) {
                            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = Uri.fromParts("package", packageName, null) })
                        }
                        showPermissionSnackbar.value = false
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        if (items.any { it.route == currentRoute }) {
                            NavigationBar(containerColor = BlueDark) {
                                items.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = null) },
                                        label = { Text(stringResource(screen.titleResId)) },
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    val forecast by homeViewModel.forecastState.collectAsStateWithLifecycle()
                    val settings by homeViewModel.settings.collectAsStateWithLifecycle()
                    val favoritesState by favoritesViewModel.favoritesState.collectAsStateWithLifecycle()

                    NavHost(navController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
                        composable(Screen.Home.route) {
                            if (forecast != null && settings != null) HomeScreen(forecast!!, settings!!)
                            else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        }

                        composable(Screen.Favorites.route) {
                            FavoritesScreen(
                                viewModel = favoritesViewModel,
                                settingsViewModel = settingsViewModel,
                                snackbarHostState = snackbarHostState,
                                onCardClick = { forecast -> navController.navigate("details/${forecast.city.id}") }
                            )
                        }

                        composable(
                            route = "details/{cityId}",
                            arguments = listOf(navArgument("cityId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val cityId = backStackEntry.arguments?.getInt("cityId")
                            val favorites = (favoritesState as? UiState.Success)?.data
                            val selectedForecast = favorites?.find { it.city.id == cityId }

                            if (selectedForecast != null && settings != null) {
                                DetailsScreen(
                                    forecast = selectedForecast,
                                    settings = settings!!,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }

                        composable(Screen.Alerts.route) {
                            val weatherDescription = forecast?.list?.firstOrNull()?.weather?.firstOrNull()?.description ?: "Weather update"
                            AlertsScreen(alertsViewModel, weatherDescription)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(settingsViewModel)
                        }
                    }
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}