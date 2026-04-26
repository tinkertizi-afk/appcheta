package com.selffocus.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import com.selffocus.presentation.ui.screens.DashboardScreen
import com.selffocus.presentation.ui.screens.FocusScreen
import com.selffocus.presentation.ui.screens.LimitsScreen
import com.selffocus.presentation.ui.screens.SettingsScreen
import com.selffocus.presentation.viewmodel.DashboardViewModel
import com.selffocus.presentation.viewmodel.FocusViewModel
import com.selffocus.presentation.viewmodel.LimitsViewModel
import com.selffocus.presentation.viewmodel.SettingsViewModel

/**
 * Main navigation graph for the app.
 */
@Composable
fun AppNavGraph() {
    val navController = androidx.navigation.compose.rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            val viewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToLimits = { navController.navigate("limits") },
                onNavigateToFocus = { navController.navigate("focus") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("limits") {
            val viewModel: LimitsViewModel = hiltViewModel()
            LimitsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("focus") {
            val viewModel: FocusViewModel = hiltViewModel()
            FocusScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
