package com.senac.travelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Menu Principal",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { }) {
            Text("Minhas Viagens")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { }) {
            Text("Nova Viagem")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { }) {
            Text("Perfil")
        }
    }
}