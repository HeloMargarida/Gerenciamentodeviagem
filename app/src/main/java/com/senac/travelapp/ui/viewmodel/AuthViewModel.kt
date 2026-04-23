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

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToMenu: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository by lazy {
        UserRepository(AppDatabase.getInstance(application).userDao())
    }

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    // ── Login buscando no Room ────────────────────────────────────────────
    fun login(email: String, senha: String) {
        val emailNormalizado = email.trim().lowercase()

        if (emailNormalizado.isEmpty() || senha.isEmpty()) {
            _loginState.value = LoginUiState(errorMessage = "Preencha todos os campos.")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginUiState(isLoading = true)

            val user = repository.getUserByEmail(emailNormalizado)

            _loginState.value = when {
                user == null -> LoginUiState(errorMessage = "Usuário não cadastrado.")
                user.senha != senha -> LoginUiState(errorMessage = "Senha incorreta.")
                else -> LoginUiState(navigateToMenu = true)
            }
        }
    }

    fun onNavigatedToMenu() {
        _loginState.value = LoginUiState()
    }

    // ── Forgot Password buscando no Room ──────────────────────────────────
    fun forgotPassword(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email.trim().lowercase())
            onResult(user != null)
        }
    }
}