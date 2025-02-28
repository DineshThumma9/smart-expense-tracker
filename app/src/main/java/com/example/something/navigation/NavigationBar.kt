package com.example.something.navigation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.something.screens.Screens


@Composable
fun BottomNavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination?.route


    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC), Color(0xFF2196F3))
    )
    NavigationBar(
        modifier = modifier.background(Color.Transparent),
        containerColor = androidx.compose.ui.graphics.Color.Transparent

    ) {
        // Home Navigation Item
        Log.d("BottomNavigationBar", "Current Destination: $currentDestination")
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentDestination == Screens.Home.route,
            onClick = {
                if (currentDestination != Screens.Home.route) {
                    navController.navigate(Screens.Home.route)
                }
            }
        )

        // Settings Navigation Item
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentDestination == Screens.Settings.route,
            onClick = {
                if (currentDestination != Screens.Settings.route) {
                    navController.navigate(Screens.Settings.route)
                }
            }
        )



        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Outlined.Science, contentDescription = "Analysis") },
            label = { Text("Analysis") },
            selected = currentDestination == Screens.Analysis.route,
            onClick = {
                if (currentDestination != Screens.Analysis.route) {
                    navController.navigate(Screens.Analysis.route)
                }
            }
        )

    }
}
