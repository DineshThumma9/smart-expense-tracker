package com.example.something

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.something.db.cloud.PaymentFirestoreDao
import com.example.something.navigation.AuthNavGraph
import com.example.something.navigation.BottomNavigationBar
import com.example.something.navigation.MainNavGraph
import com.example.something.screens.AnalysisScreen
import com.example.something.screens.AnalysisViewModel
import com.example.something.ui.theme.SomethingTheme
import com.example.something.viewmodel.AuthViewModel
import com.example.something.viewmodel.PaymentViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    // Register an ActivityResultLauncher for overlay permission
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Optionally, handle the result. For example, check if overlay permission was granted:
        if (!Settings.canDrawOverlays(this)) {
            // You may notify the user or take other action if permission is still not granted.
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request overlay permission if necessary using the new API
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        }

        enableEdgeToEdge()

        // Initialize your local database (Room) and DAOs
        val database = applicationContext.getDatabase()

        // Use the new Firestore DAO which generates document IDs automatically
        val paymentFirestoreDao = PaymentFirestoreDao()

        // Create the PaymentViewModel with all required DAOs
        val paymentViewModel = PaymentViewModel(
            paymentDao = database.paymentDao(),
            tagDao = database.tagDao(),
            paymentWithTagsDao = database.paymentWithTagsDao(),
            crossRefDao = database.paymentTagsCrossRefDao(),
            paymentFirestoreDao = paymentFirestoreDao
        )

        // Create the AnalysisViewModel using the same Firestore DAO
        val analysisViewModel = AnalysisViewModel(
            modifier = Modifier,
            paymentFirestoreDao = paymentFirestoreDao
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
