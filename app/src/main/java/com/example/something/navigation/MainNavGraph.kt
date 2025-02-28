package com.example.something.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.something.screens.AnalysisScreen
import com.example.something.screens.AnalysisViewModel
import com.example.something.screens.HomeScreen
import com.example.something.screens.PaymentScreen
import com.example.something.screens.Screens
import com.example.something.screens.SettingsScreen
import com.example.something.viewmodel.AuthViewModel
import com.example.something.viewmodel.PaymentViewModel


@Composable
fun MainNavGraph(
    navController: NavHostController,
    paymentViewModel: PaymentViewModel,
    analysisViewModel: AnalysisViewModel,
    authViewModel: AuthViewModel
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.Home.route) {
                HomeScreen(navController = navController, viewModel = paymentViewModel)
            }


            composable(Screens.Analysis.route) {
                AnalysisScreen(navController = navController, viewModel = analysisViewModel)
            }

            composable(Screens.Settings.route) {
                SettingsScreen(navController = navController)
            }

            composable(Screens.Payment.route) {
                PaymentScreen(navController = navController, viewModel = paymentViewModel)
            }
            // Define other main screens
        }
    }
}
