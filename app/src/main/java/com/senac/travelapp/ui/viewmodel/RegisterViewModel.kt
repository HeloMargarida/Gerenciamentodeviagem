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

data class RegisterUiState(
    val nome: String = "",
    val email: String = "",
    val telefone: String = "",
    val senha: String = "",
    val confirmar: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val navigateToLogin: Boolean = false
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository by lazy {
        UserRepository(AppDatabase.getInstance(application).userDao())
    }

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // ── Atualização dos campos ────────────────────────────────────────────
    fun onNomeChange(value: String) {
        _uiState.value = _uiState.value.copy(nome = value, errorMessage = null)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onTelefoneChange(value: String) {
        _uiState.value = _uiState.value.copy(telefone = value, errorMessage = null)
    }

    fun onSenhaChange(value: String) {
        _uiState.value = _uiState.value.copy(senha = value, errorMessage = null)
    }

    fun onConfirmarChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmar = value, errorMessage = null)
    }

    // ── Lógica de registro ───────────────────────────────────────────────
    fun register() {
        val state = _uiState.value

        // Validações
        if (state.nome.isBlank() || state.email.isBlank() ||
            state.telefone.isBlank() || state.senha.isBlank()
        ) {
            _uiState.value = state.copy(errorMessage = "Preencha todos os campos.")
            return
        }

        if (state.senha != state.confirmar) {
            _uiState.value = state.copy(errorMessage = "As senhas não coincidem.")
            return
        }

        val emailNormalizado = state.email.trim().lowercase()

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)

            val result = repository.registerUser(
                nome = state.nome.trim(),
                email = emailNormalizado,
                telefone = state.telefone.trim(),
                senha = state.senha
            )

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Cadastro realizado com sucesso!"
                )
            } else {
                val msg = if (result.exceptionOrNull()?.message?.contains("UNIQUE") == true)
                    "Este e-mail já está cadastrado."
                else
                    "Erro ao cadastrar. Tente novamente."
                _uiState.value.copy(isLoading = false, errorMessage = msg)
            }
        }
    }

    // ── Navegação ────────────────────────────────────────────────────────
    fun onSuccessAcknowledged() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            navigateToLogin = true
        )
    }

    fun onNavigatedToLogin() {
        _uiState.value = RegisterUiState()
    }
}
