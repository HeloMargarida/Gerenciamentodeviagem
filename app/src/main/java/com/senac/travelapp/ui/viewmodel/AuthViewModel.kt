package com.senac.travelapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.senac.travelapp.data.local.AppDatabase
import com.senac.travelapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class User(
    val nome: String,
    val email: String,
    val telefone: String,
    val senha: String
)

data class RegisterUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val navigateToLogin: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // ── Room ──────────────────────────────────────────────────────────────
    private val repository: UserRepository by lazy {
        UserRepository(AppDatabase.getInstance(application).userDao())
    }

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    // ── Estado em memória (login/forgotPassword — lógica original mantida) ─
    private var loggedUser: User? = null

    // ── Register com Room ─────────────────────────────────────────────────
    fun register(
        nome: String,
        email: String,
        telefone: String,
        senha: String,
        confirmar: String
    ) {
        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || senha.isEmpty()) {
            _registerState.value = RegisterUiState(errorMessage = "Preencha todos os campos.")
            return
        }
        if (senha != confirmar) {
            _registerState.value = RegisterUiState(errorMessage = "As senhas não coincidem.")
            return
        }

        val emailNormalizado = email.trim().lowercase()

        viewModelScope.launch {
            _registerState.value = RegisterUiState(isLoading = true)

            val result = repository.registerUser(
                nome = nome.trim(),
                email = emailNormalizado,
                telefone = telefone.trim(),
                senha = senha
            )

            _registerState.value = if (result.isSuccess) {
                // Mantém o usuário em memória para o login funcionar imediatamente
                loggedUser = User(nome.trim(), emailNormalizado, telefone.trim(), senha)
                RegisterUiState(successMessage = "Cadastro realizado com sucesso!")
            } else {
                val msg = if (result.exceptionOrNull()?.message?.contains("UNIQUE") == true)
                    "Este e-mail já está cadastrado."
                else
                    "Erro ao cadastrar. Tente novamente."
                RegisterUiState(errorMessage = msg)
            }
        }
    }

    /** Chamado após o Snackbar de sucesso ser exibido */
    fun onSuccessAcknowledged() {
        _registerState.value = _registerState.value.copy(
            successMessage = null,
            navigateToLogin = true
        )
    }

    /** Chamado após a navegação para limpar o flag */
    fun onNavigatedToLogin() {
        _registerState.value = RegisterUiState()
    }

    // ── Login (lógica original mantida) ───────────────────────────────────
    fun login(email: String, senha: String): Boolean {
        val emailDigitado = email.trim().lowercase()
        val emailSalvo = loggedUser?.email
        return emailSalvo == emailDigitado && loggedUser?.senha == senha
    }

    // ── Forgot Password (lógica original mantida) ─────────────────────────
    fun forgotPassword(email: String): Boolean {
        val emailDigitado = email.trim().lowercase()
        return loggedUser?.email == emailDigitado
    }
}
