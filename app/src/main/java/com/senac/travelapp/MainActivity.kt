package com.senac.travelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.senac.travelapp.navigation.AppNavigation
import com.senac.travelapp.ui.theme.TravelAppTheme
import com.senac.travelapp.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelAppTheme {

                // 🔥 ViewModel único para todo o app
                val viewModel: AuthViewModel = viewModel()
                AppNavigation(viewModel)
            }
        }
    }
}