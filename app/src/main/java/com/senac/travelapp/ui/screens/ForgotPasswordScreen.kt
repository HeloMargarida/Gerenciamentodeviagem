package com.senac.travelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.senac.travelapp.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel          // ✅ Usa apenas o parâmetro, sem redeclarar
) {
    var email by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }
    var sucesso by remember { mutableStateOf(false) }   // ✅ Estado de sucesso

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar Senha") },
                navigationIcon = {
                    // ✅ Botão de voltar na TopBar
                    IconButton(onClick = { navController.navigate("login") }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar ao Login"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Digite seu e-mail cadastrado e enviaremos as instruções para redefinir sua senha.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    erro = ""       // ✅ Limpa erro ao digitar
                    sucesso = false
                },
                label = { Text("E-mail") },
                singleLine = true,
                isError = erro.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ✅ Mensagem de erro
            if (erro.isNotEmpty()) {
                Text(
                    text = erro,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ✅ Mensagem de sucesso
            if (sucesso) {
                Text(
                    text = "✅ Instruções enviadas para $email",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val emailNormalizado = email.trim().lowercase()

                    when {
                        // ✅ Validação: campo vazio
                        emailNormalizado.isEmpty() -> {
                            erro = "Preencha o e-mail"
                        }
                        // ✅ Validação: formato inválido
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(emailNormalizado).matches() -> {
                            erro = "Formato de e-mail inválido"
                        }
                        // ✅ Tenta recuperar senha
                        viewModel.forgotPassword(emailNormalizado) -> {
                            erro = ""
                            sucesso = true
                        }
                        else -> {
                            erro = "E-mail não encontrado"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Enviar")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Botão secundário de voltar ao login
            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voltar ao Login")
            }
        }
    }
}