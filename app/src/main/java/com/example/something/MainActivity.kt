package com.example.something

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.something.db.cloud.PaymentMongoDao
import com.example.something.navigation.AuthNavGraph
import com.example.something.navigation.MainNavGraph
import com.example.something.navigation.BottomNavigationBar
import com.example.something.screens.AnalysisScreen
import com.example.something.screens.AnalysisViewModel
import com.example.something.ui.theme.SomethingTheme
import com.example.something.viewmodel.AuthViewModel
import com.example.something.viewmodel.PaymentViewModel

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request overlay permission if necessary
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 101)
        }

        enableEdgeToEdge()

        val database = applicationContext.getDatabase()
        val paymentMongoDao = PaymentMongoDao()

        val paymentViewModel = PaymentViewModel(
            paymentDao = database.paymentDao(),
            tagDao = database.tagDao(),
            paymentWithTagsDao = database.paymentWithTagsDao(),
            crossRefDao = database.paymentTagsCrossRefDao(),
            paymentMongodao = paymentMongoDao
        )

        val analysisViewModel = AnalysisViewModel(
            paymentMongoDao = PaymentMongoDao(),
            modifier = Modifier
        )

        val authViewModel = AuthViewModel()

        setContent {
            SomethingTheme {
                val navController = rememberNavController()
                // Observe the authentication state from your ViewModel
                val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

                // Switch completely between auth and main flows using Crossfade
                Crossfade(targetState = isAuthenticated) { authenticated ->
                    if (authenticated) {
                        // Main flow – includes a Scaffold with a BottomNavigationBar
                        Scaffold(
                            bottomBar = {
                                BottomNavigationBar(navController = navController)
                            }
                        ) { innerPadding ->
                            MainNavGraph(
                                navController = navController,
                                paymentViewModel = paymentViewModel,
                                analysisViewModel = analysisViewModel,
                                authViewModel = authViewModel

                            )
                        }
                    } else {
                        // Auth flow – no bottom bar; the entire auth stack is shown.
                        AuthNavGraph(
                            navController = navController,
                            authViewModel = authViewModel
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalysisPreview() {
    SomethingTheme {
        AnalysisScreen(
            navController = rememberNavController(),
            viewModel = AnalysisViewModel(modifier = Modifier, paymentMongoDao = PaymentMongoDao())
        )
    }
}
