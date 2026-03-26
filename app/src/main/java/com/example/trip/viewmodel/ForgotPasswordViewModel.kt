package com.example.trip.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import android.util.Patterns

class ForgotPasswordViewModel : ViewModel() {

    var email = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)

    fun onEmailChange(value: String) {
        email.value = value
    }

    private fun validate(): Boolean {

        if (email.value.isBlank()) {
            errorMessage.value = "Informe o e-mail"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            errorMessage.value = "E-mail inválido"
            return false
        }

        errorMessage.value = null
        return true
    }

    fun onSubmit(onSuccess: () -> Unit) {
        if (validate()) {
            println("Recuperação enviada para: ${email.value}")
            onSuccess()
        }
    }
}