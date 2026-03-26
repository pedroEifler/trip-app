package com.example.trip.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var phone = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")

    var errorMessage = mutableStateOf<String?>(null)

    fun validate(): Boolean {

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

        if (password.value != confirmPassword.value) {
            errorMessage.value = "As senhas não coincidem"
            return false
        }

        errorMessage.value = null
        return true
    }

    fun onRegister(onSuccess: () -> Unit) {
        if (validate()) {
            onSuccess()
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

}