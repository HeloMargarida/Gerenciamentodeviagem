package com.senac.travelapp.data.repository

import com.senac.travelapp.data.local.dao.UserDao
import com.senac.travelapp.data.local.entity.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(
        nome: String,
        email: String,
        telefone: String,
        senha: String
    ): Result<Long> {
        return try {
            val user = UserEntity(
                nome = nome,
                email = email,
                telefone = telefone,
                senha = senha
            )
            val id = userDao.insertUser(user)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
}
