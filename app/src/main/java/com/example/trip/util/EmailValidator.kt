package com.example.trip.util

import android.util.Patterns

fun isValidEmail(email: String): Boolean {
    return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validateEmail(email: String): String? {
    if (email.isBlank()) return "Informe o e-mail"
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "E-mail inválido"
    return null
}

