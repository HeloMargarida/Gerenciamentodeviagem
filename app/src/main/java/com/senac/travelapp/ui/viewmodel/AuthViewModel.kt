package com.senac.travelapp.ui.viewmodel

import androidx.lifecycle.ViewModel

data class User(
    val nome: String,
    val email: String,
    val telefone: String,
    val senha: String
)

class AuthViewModel : ViewModel() {

    private var user: User? = null

    fun register(
        nome: String,
        email: String,
        telefone: String,
        senha: String,
        confirmar: String
    ): Boolean {

        if (
            nome.isEmpty() ||
            email.isEmpty() ||
            telefone.isEmpty() ||
            senha.isEmpty() ||
            senha != confirmar
        ) return false

        // 🔥 NORMALIZA AO SALVAR
        val emailNormalizado = email.trim().lowercase()

        user = User(nome, emailNormalizado, telefone, senha)

        return true
    }

    fun login(email: String, senha: String): Boolean {
        val emailDigitado = email.trim().lowercase()
        val emailSalvo = user?.email

        return emailSalvo == emailDigitado && user?.senha == senha
    }

    fun forgotPassword(email: String): Boolean {
        val emailDigitado = email.trim().lowercase()
        val emailSalvo = user?.email

        return emailSalvo == emailDigitado
    }
}