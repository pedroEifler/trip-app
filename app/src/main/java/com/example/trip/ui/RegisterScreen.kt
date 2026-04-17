package com.example.trip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trip.ui.components.EmailField
import com.example.trip.ui.components.InputField
import com.example.trip.ui.components.PasswordField
import com.example.trip.viewmodel.RegisterViewModel
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    vm: RegisterViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(vm.successMessage.value) {
        vm.successMessage.value?.let { message ->
            snackbarHostState.showSnackbar(message)
            delay(500)
            onNavigateBack()
            vm.successMessage.value = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Cadastro",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                InputField(vm.name.value, vm::onNameChange, "Nome")
                Spacer(modifier = Modifier.height(12.dp))

                EmailField(vm.email.value, vm::onEmailChange)
                Spacer(modifier = Modifier.height(12.dp))

                InputField(vm.phone.value, vm::onPhoneChange, "Telefone")
                Spacer(modifier = Modifier.height(12.dp))

                PasswordField(vm.password.value, vm::onPasswordChange)
                Spacer(modifier = Modifier.height(12.dp))

                PasswordField(vm.confirmPassword.value, vm::onConfirmPasswordChange)

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
                        vm.onRegister {}
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !vm.isLoading.value
                ) {
                    Text("Registrar")
                }
            }

            if (vm.isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}