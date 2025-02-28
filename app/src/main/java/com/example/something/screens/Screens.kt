package com.example.something.screens

sealed class Screens(val route: String) {
    // Auth Screens
    object Login : Screens("login")
    object SignUp : Screens("signup")

    // Main Screens
    object Home : Screens("home")
    object Payment : Screens("add_payment")
    object Settings : Screens("settings")
    object Analysis : Screens("analysis")

}


