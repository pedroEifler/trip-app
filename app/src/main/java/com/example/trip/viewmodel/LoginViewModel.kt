package com.example.trip.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trip.data.repository.UserRepository
import com.example.trip.util.validateEmail
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    var email = mutableStateOf("pedro@gmail.com")
    var password = mutableStateOf("123")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    fun onEmailChange(value: String) {
        email.value = value
    }

    fun onPasswordChange(value: String) {
        password.value = value
    }

    private fun validate(): Boolean {
        val emailError = validateEmail(email.value)
        if (emailError != null) {
            errorMessage.value = emailError
            return false
        }

        if (password.value.isBlank()) {
            errorMessage.value = "Informe a senha"
            return false
        }

        errorMessage.value = null
        return true
    }

    fun onLogin(onSuccess: (email: String, password: String) -> Unit) {
        if (!validate()) return

        isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.login(email.value, password.value)) {
                is UserRepository.LoginResult.Success -> {
                    errorMessage.value = null
                    onSuccess(result.user.email, result.user.password)
                }
                is UserRepository.LoginResult.InvalidCredentials -> {
                    errorMessage.value = "E-mail ou senha inválidos"
                }
                is UserRepository.LoginResult.Error -> {
                    errorMessage.value = result.message
                }
            }
            isLoading.value = false
        }
    }

    companion object {
        fun provideFactory(repository: UserRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoginViewModel(repository) as T
                }
            }
        }
    }
}