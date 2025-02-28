package com.example.something.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.something.screens.Screens
import com.example.something.screens.SignInScreen
import com.example.something.screens.SignUpScreen
import com.example.something.viewmodel.AuthViewModel

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Login.route
    ) {
        composable(Screens.Login.route) {
            SignInScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }
        composable(Screens.SignUp.route) {
            SignUpScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }
    }
}
