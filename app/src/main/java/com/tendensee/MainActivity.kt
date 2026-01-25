package com.tendensee

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tendensee.ui.home.HomeScreen
import com.tendensee.ui.navigation.Screen
import com.tendensee.ui.stats.StatsScreen
import com.tendensee.viewmodel.HabitViewModel
import com.tendensee.ui.theme.TendenSeeTheme
import com.tendensee.ui.SplashScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.app.ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

        setContent {
            TendenSeeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(true) }
                    
                    LaunchedEffect(Unit) {
                        delay(2000)
                        showSplash = false
                    }

                    if (showSplash) {
                        SplashScreen()
                    } else {
                        val navController = rememberNavController()
                        val viewModel: HabitViewModel = viewModel(
                            factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                        )

                        NavHost(navController = navController, startDestination = Screen.Home.route) {
                            composable(Screen.Home.route) {
                                HomeScreen(navController = navController, viewModel = viewModel)
                            }
                            composable(Screen.AddHabit.route) {
                                com.tendensee.ui.habit.AddHabitScreen(navController = navController, viewModel = viewModel)
                            }
                            composable(
                                route = Screen.HabitDetail.route,
                                arguments = listOf(androidx.navigation.navArgument("habitId") { type = androidx.navigation.NavType.IntType })
                            ) { backStackEntry ->
                                val habitId = backStackEntry.arguments?.getInt("habitId") ?: return@composable
                                com.tendensee.ui.habit.HabitDetailScreen(navController = navController, viewModel = viewModel, habitId = habitId)
                            }
                            composable(Screen.Stats.route) {
                                StatsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}