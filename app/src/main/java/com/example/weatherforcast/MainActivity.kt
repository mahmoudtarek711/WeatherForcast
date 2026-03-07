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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.routes.Screen
import com.example.weatherforcast.ui.screens.*
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.ui.viewmodels.AlertsViewModel
import com.example.weatherforcast.ui.viewmodels.HomeViewModel
import com.example.weatherforcast.utils.LocationProvider
import com.example.weatherforcast.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // 1. State to track if permission was denied to trigger the snackbar
    private val showPermissionSnackbar = mutableStateOf(false)

    // 2. Launcher to request POST_NOTIFICATIONS (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            showPermissionSnackbar.value = true
        }
    }
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            // Refresh weather now that we have permission
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check permission immediately on start
        checkNotificationPermission()

        setContent {
            WeatherForcastTheme {
                // --- Dependencies Setup ---
                val database = AppDatabase.getInstance(this)
                val settingsManager = remember { SettingsManager(this) }
                val repo = remember {
                    ForcastRepository(
                        ForcastRemoteDataSource(),
                        ForcastLocalDataSource(this),
                        database.alertsDao()
                    )
                }

                // --- ViewModel Initializations (Factories) ---
                val alertsViewModel: AlertsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AlertsViewModel(application, repo) as T
                        }
                    }
                )

                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return SettingsViewModel(settingsManager) as T
                        }
                    }
                )

                val homeViewModel: HomeViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return HomeViewModel(repo, settingsManager, LocationProvider(this@MainActivity),this@MainActivity) as T
                        }
                    }
                )

                LaunchedEffect(Unit) {
                    locationPermissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                }


                // --- UI State Helpers ---
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val items = listOf(Screen.Home, Screen.Favorites, Screen.Alerts, Screen.Settings)


                LaunchedEffect(Unit) {
                    homeViewModel.errorEvents.collect { message ->
                        snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                // --- Permission Snackbar Logic ---
                if (showPermissionSnackbar.value) {
                    LaunchedEffect(snackbarHostState) {
                        val result = snackbarHostState.showSnackbar(
                            message = "Notifications are required for Weather Alerts",
                            actionLabel = "Settings",
                            duration = SnackbarDuration.Long
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            // Open App Settings so user can enable notifications manually
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", packageName, null)
                            }
                            startActivity(intent)
                        }
                        showPermissionSnackbar.value = false
                    }
                }
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                    ,
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
                    val forecast by homeViewModel.forecastState.collectAsStateWithLifecycle()
                    val settings by homeViewModel.settings.collectAsStateWithLifecycle()
                    val weatherDescription =
                        forecast?.list?.firstOrNull()?.weather?.firstOrNull()?.description ?: "Weather update"
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            if (forecast != null && settings != null) {
                                HomeScreen(forecast!!, settings!!)
                            } else {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color.White)
                                }
                            }
                        }
                        composable(Screen.Favorites.route) { FavoritesScreen() }
                        composable(Screen.Alerts.route) {
                            AlertsScreen(viewModel = alertsViewModel,
                            weatherDescription = weatherDescription)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(viewModel = settingsViewModel)
                        }
                    }
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}