package com.bandverse.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bandverse.app.ui.create.CreateRecommendationScreen
import com.bandverse.app.ui.history.HistoryScreen
import com.bandverse.app.ui.home.HomeScreen
import com.bandverse.app.ui.profile.ProfileScreen

@Composable
fun BandverseNavGraph(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Create.route) {
                CreateRecommendationScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
