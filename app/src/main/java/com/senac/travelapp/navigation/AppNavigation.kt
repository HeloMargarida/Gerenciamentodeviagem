package com.senac.travelapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.senac.travelapp.ui.screens.ForgotPasswordScreen
import com.senac.travelapp.ui.screens.LoginScreen
import com.senac.travelapp.ui.screens.MenuScreen
import com.senac.travelapp.ui.screens.RegisterScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("forgot") {
            ForgotPasswordScreen(navController)
        }

        composable("menu") {
            MenuScreen()
        }
    }
}