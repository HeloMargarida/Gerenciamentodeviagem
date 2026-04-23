package com.senac.travelapp.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.senac.travelapp.ui.screens.ForgotPasswordScreen
import com.senac.travelapp.ui.screens.LoginScreen
import com.senac.travelapp.ui.screens.MenuScreen
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
            RegisterScreen(navController)  // ← sem viewModel
        }

        composable("forgot") {
            ForgotPasswordScreen(navController, viewModel)
        }

        composable("menu") {
            MenuScreen(navController)
        }

        composable("viagens") {
            Text("Tela de Viagens")
        }

        composable("nova_viagem") {
            Text("Nova Viagem")
        }

        composable("perfil") {
            Text("Perfil")
        }
    }
}
