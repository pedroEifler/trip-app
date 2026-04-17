package com.example.trip.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trip.data.repository.UserRepository
import com.example.trip.util.validateEmail
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val repository: UserRepository) : ViewModel() {

    var email = mutableStateOf("")
    var newPassword = mutableStateOf("")
    var confirmNewPassword = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    fun onEmailChange(value: String) {
        email.value = value
    }

    fun onNewPasswordChange(value: String) {
        newPassword.value = value
    }

    fun onConfirmNewPasswordChange(value: String) {
        confirmNewPassword.value = value
    }

    private fun validate(): Boolean {
        val emailError = validateEmail(email.value)
        if (emailError != null) {
            errorMessage.value = emailError
            return false
        }

        if (newPassword.value.isBlank()) {
            errorMessage.value = "Informe a nova senha"
            return false
        }

        if (newPassword.value != confirmNewPassword.value) {
            errorMessage.value = "As senhas não coincidem"
            return false
        }

        errorMessage.value = null
        return true
    }

    fun onSubmit(onSuccess: () -> Unit) {
        if (!validate()) return

        isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.resetPassword(email.value, newPassword.value)) {
                is UserRepository.ResetPasswordResult.Success -> {
                    errorMessage.value = null
                    onSuccess()
                }
                is UserRepository.ResetPasswordResult.EmailNotFound -> {
                    errorMessage.value = "E-mail não encontrado"
                }
                is UserRepository.ResetPasswordResult.Error -> {
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
                    return ForgotPasswordViewModel(repository) as T
                }
            }
        }
    }
}