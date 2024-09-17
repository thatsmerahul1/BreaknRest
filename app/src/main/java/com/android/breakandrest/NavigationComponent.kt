package com.android.breakandrest

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationComponent(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController, startDestination = "dashboard") {
//        composable("mainScreen") {
//            MainScreen(navController, viewModel)
//        }
        composable("dashboard") {
            DashboardScreen(navController, viewModel)
        }
        composable("settings") {
            SettingsScreen(navController, viewModel)
        }
    }
}