package com.senac.travelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ForgotPasswordScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Recuperar Senha",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (erro.isNotEmpty()) {
            Text(
                text = erro,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (email.isEmpty()) {
                    erro = "Digite um e-mail"
                } else {
                    erro = ""
                    navController.navigate("login")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }
    }
}