package com.example.trip.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.trip.util.validateEmail

class LoginViewModel : ViewModel() {

    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)

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
        if (validate()) {
            onSuccess(email.value, password.value)
        }
    }
}