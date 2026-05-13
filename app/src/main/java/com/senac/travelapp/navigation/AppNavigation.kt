package com.senac.travelapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.senac.travelapp.ui.screens.ForgotPasswordScreen
import com.senac.travelapp.ui.screens.LoginScreen
import com.senac.travelapp.ui.screens.MenuScreen
import com.senac.travelapp.ui.screens.MyTravelsScreen
import com.senac.travelapp.ui.screens.NewTravelScreen
import com.senac.travelapp.ui.screens.RegisterScreen
import com.senac.travelapp.ui.viewmodel.AuthViewModel

@Composable
fun AppNavigation(viewModel: AuthViewModel) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController, viewModel)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("forgot") {
            ForgotPasswordScreen(navController, viewModel)
        }

        composable("menu") {
            MenuScreen(navController)
        }

        composable("viagens") {
            MyTravelsScreen(
                navController = navController,
                authViewModel = viewModel
            )
        }

        composable("nova_viagem") {
            NewTravelScreen(
                navController = navController,
                authViewModel = viewModel
            )
        }

        composable("sobre") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Travel App v1.0\nDesenvolvido para o trabalho de SENAC.")
            }
        }
    }
}
