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

        user = User(nome, email, telefone, senha)
        return true
    }

    fun login(email: String, senha: String): Boolean {
        return user?.email == email && user?.senha == senha
    }

    fun forgotPassword(email: String): Boolean {
        return user?.email == email
    }
}