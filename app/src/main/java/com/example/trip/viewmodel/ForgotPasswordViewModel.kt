package com.example.trip.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.trip.util.validateEmail

class ForgotPasswordViewModel : ViewModel() {

    var email = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)

    fun onEmailChange(value: String) {
        email.value = value
    }

    private fun validate(): Boolean {
        val emailError = validateEmail(email.value)
        if (emailError != null) {
            errorMessage.value = emailError
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