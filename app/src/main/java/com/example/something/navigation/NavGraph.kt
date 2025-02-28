/*
package com.example.something.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.something.db.cloud.PaymentMongoDao
import com.example.something.screens.AnalysisScreen
import com.example.something.screens.AnalysisViewModel
import com.example.something.screens.HomeScreen
import com.example.something.screens.PaymentScreen
import com.example.something.screens.Screens
import com.example.something.screens.SettingsScreen
import com.example.something.screens.SignInScreen
import com.example.something.screens.SignUpScreen
import com.example.something.viewmodel.AuthViewModel
import com.example.something.viewmodel.PaymentViewModel
import com.google.android.play.core.integrity.au

@Composable
fun NavGraph(navController: NavHostController,paymentviewModel: PaymentViewModel,modifier: Modifier,analysisviewmodel : AnalysisViewModel,authViewModel : AuthViewModel) {
    NavHost(navController = navController, startDestination = Screens.Login.route) {



        composable(Screens.Login.route) {
            SignInScreen(
                navController = navController
            )
        }


        composable(Screens.SignUp.route) {
            SignUpScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }



        composable(Screens.Home.route) {
            HomeScreen(
                navController = navController,
                viewModel = paymentviewModel// Corrected: Use viewModel() instead of rememberviewModel()
            )
        }

        composable(Screens.Analysis.route){
            AnalysisScreen(
                navController = navController,
                viewModel = analysisviewmodel
            )
        }

        composable(Screens.Settings.route){
            SettingsScreen(navController = navController)
        }



        composable(Screens.Payment.route) {
            PaymentScreen(
                navController = navController,
                viewModel = paymentviewModel// Pass the ViewModel
            )
        }
    }
}*/
