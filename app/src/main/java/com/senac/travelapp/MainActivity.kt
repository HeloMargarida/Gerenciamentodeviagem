package com.senac.travelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.senac.travelapp.navigation.AppNavigation
import com.senac.travelapp.ui.theme.TravelAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelAppTheme {
                AppNavigation()
            }
        }
    }
}