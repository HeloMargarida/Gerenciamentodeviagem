package com.senac.travelapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.senac.travelapp.ui.viewmodel.AuthViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf("") }

    val viewModel: AuthViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Login",
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

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = if (showPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                val icon = if (showPassword)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(imageVector = icon, contentDescription = "Toggle password")
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { showPassword = !showPassword },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mostrar/Esconder Senha")
        }

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
                if (viewModel.login(email, senha)) {
                    erro = ""
                    navController.navigate("menu")
                } else {
                    erro = "Preencha todos os campos"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Novo Usuário")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("forgot") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Esqueci a senha")
        }
    }
}