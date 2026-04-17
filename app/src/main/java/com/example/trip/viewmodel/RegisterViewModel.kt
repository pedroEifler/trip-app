package com.example.trip.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trip.data.repository.UserRepository
import com.example.trip.util.validateEmail
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var phone = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")

    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)
    var successMessage = mutableStateOf<String?>(null)

    private fun validate(): Boolean {
        if (
            name.value.isBlank() ||
            email.value.isBlank() ||
            phone.value.isBlank() ||
            password.value.isBlank() ||
            confirmPassword.value.isBlank()
        ) {
            errorMessage.value = "Preencha todos os campos"
            return false
        }

        val emailError = validateEmail(email.value)
        if (emailError != null) {
            errorMessage.value = emailError
            return false
        }

        if (password.value != confirmPassword.value) {
            errorMessage.value = "As senhas não coincidem"
            return false
        }

        errorMessage.value = null
        return true
    }

    fun onRegister(onSuccess: () -> Unit) {
        if (!validate()) return

        isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.register(
                name = name.value,
                email = email.value,
                phone = phone.value,
                password = password.value
            )) {
                is UserRepository.RegisterResult.Success -> {
                    errorMessage.value = null
                    successMessage.value = "Registro bem-sucedido!"
                    onSuccess()
                }
                is UserRepository.RegisterResult.EmailAlreadyExists -> {
                    errorMessage.value = "Este e-mail já está cadastrado"
                }
                is UserRepository.RegisterResult.Error -> {
                    errorMessage.value = result.message
                }
            }
            isLoading.value = false
        }
    }

    fun onNameChange(value: String) {
        name.value = value
    }

    fun onEmailChange(value: String) {
        email.value = value
    }

    fun onPhoneChange(value: String) {
        phone.value = value
    }

    fun onPasswordChange(value: String) {
        password.value = value
    }

    fun onConfirmPasswordChange(value: String) {
        confirmPassword.value = value
    }

    companion object {
        fun provideFactory(repository: UserRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RegisterViewModel(repository) as T
                }
            }
        }
    }
}