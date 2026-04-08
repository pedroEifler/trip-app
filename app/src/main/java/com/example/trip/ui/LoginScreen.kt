package com.example.trip.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trip.R
import com.example.trip.ui.components.EmailField
import com.example.trip.ui.components.LoginButton
import com.example.trip.ui.components.PasswordField
import com.example.trip.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLogin: (email: String, password: String) -> Unit,
    vm: LoginViewModel = viewModel()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Imagem viagem",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        EmailField(
            value = vm.email.value,
            onChange = vm::onEmailChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        PasswordField(
            value = vm.password.value,
            onChange = vm::onPasswordChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        vm.errorMessage.value?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        LoginButton(
            onClick = { vm.onLogin(onLogin) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Novo usuário",
                modifier = Modifier.clickable {
                    onNavigateToRegister()
                }
            )

            Text(
                text = "Esqueci a senha",
                modifier = Modifier.clickable {
                    onNavigateToForgotPassword()
                }
            )
        }
    }
}