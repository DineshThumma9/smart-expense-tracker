package com.example.something.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.something.screens.AnalysisViewModel
import com.example.something.viewmodel.AuthViewModel
import com.example.something.viewmodel.PaymentViewModel

@Composable
fun AppNavigator(
    authViewModel: AuthViewModel,
    paymentViewModel: PaymentViewModel,
    analysisViewModel: AnalysisViewModel
) {
    val navController = rememberNavController()
    // Collect the authentication state (make sure your auth state is a StateFlow or LiveData that converts to Compose state)
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    // Crossfade provides a smooth transition between the two UI trees
    Crossfade(targetState = isAuthenticated) { authenticated ->
        if (authenticated) {
            MainNavGraph(
                navController = navController,
                paymentViewModel = paymentViewModel,
                analysisViewModel = analysisViewModel,
                authViewModel = authViewModel
            )
        } else {
            AuthNavGraph(navController = navController, authViewModel = authViewModel)
        }
    }
}
