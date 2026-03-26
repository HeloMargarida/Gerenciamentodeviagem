package com.senac.travelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RegisterScreen(navController: NavController) {

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }

    var erro by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Cadastro",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefone,
            onValueChange = { telefone = it },
            label = { Text("Telefone") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmar,
            onValueChange = { confirmar = it },
            label = { Text("Confirmar Senha") },
            visualTransformation = PasswordVisualTransformation(),
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
                if (
                    nome.isEmpty() ||
                    email.isEmpty() ||
                    telefone.isEmpty() ||
                    senha.isEmpty() ||
                    confirmar.isEmpty()
                ) {
                    erro = "Preencha todos os campos"
                } else if (senha != confirmar) {
                    erro = "As senhas não coincidem"
                } else {
                    erro = ""
                    navController.navigate("login")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }
    }
}