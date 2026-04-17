package com.example.trip.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.trip.data.local.dao.UserDao
import com.example.trip.data.local.entity.UserEntity

class UserRepository(private val userDao: UserDao) {

    sealed class RegisterResult {
        data class Success(val userId: Long) : RegisterResult()
        data object EmailAlreadyExists : RegisterResult()
        data class Error(val message: String) : RegisterResult()
    }

    sealed class LoginResult {
        data class Success(val user: UserEntity) : LoginResult()
        data object InvalidCredentials : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    sealed class ResetPasswordResult {
        data object Success : ResetPasswordResult()
        data object EmailNotFound : ResetPasswordResult()
        data class Error(val message: String) : ResetPasswordResult()
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): RegisterResult {
        return try {
            val user = UserEntity(
                name = name,
                email = email,
                phone = phone,
                password = password
            )
            val userId = userDao.insert(user)
            RegisterResult.Success(userId)
        } catch (e: SQLiteConstraintException) {
            RegisterResult.EmailAlreadyExists
        } catch (e: Exception) {
            RegisterResult.Error(e.message ?: "Erro desconhecido")
        }
    }

    suspend fun login(email: String, password: String): LoginResult {
        return try {
            val user = userDao.login(email, password)
            if (user != null) {
                LoginResult.Success(user)
            } else {
                LoginResult.InvalidCredentials
            }
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "Erro desconhecido")
        }
    }

    suspend fun resetPassword(email: String, newPassword: String): ResetPasswordResult {
        return try {
            val rowsUpdated = userDao.updatePasswordByEmail(email, newPassword)
            if (rowsUpdated > 0) {
                ResetPasswordResult.Success
            } else {
                ResetPasswordResult.EmailNotFound
            }
        } catch (e: Exception) {
            ResetPasswordResult.Error(e.message ?: "Erro desconhecido")
        }
    }

    suspend fun findByEmail(email: String): UserEntity? {
        return userDao.findByEmail(email)
    }
}

