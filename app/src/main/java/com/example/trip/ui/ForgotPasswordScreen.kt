package com.example.trip.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trip.ui.components.EmailField
import com.example.trip.ui.components.PasswordField
import com.example.trip.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    vm: ForgotPasswordViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Recuperar senha",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            EmailField(
                value = vm.email.value,
                onChange = vm::onEmailChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordField(
                value = vm.newPassword.value,
                onChange = vm::onNewPasswordChange,
                label = "Nova senha"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordField(
                value = vm.confirmNewPassword.value,
                onChange = vm::onConfirmNewPasswordChange,
                label = "Confirmar nova senha"
            )

            Spacer(modifier = Modifier.height(16.dp))

            vm.errorMessage.value?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    vm.onSubmit {
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !vm.isLoading.value
            ) {
                Text("Redefinir senha")
            }
        }

        if (vm.isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}