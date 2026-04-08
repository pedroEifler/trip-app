package com.example.trip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.trip.ui.ForgotPasswordScreen
import com.example.trip.ui.HomeScreen
import com.example.trip.ui.LoginScreen
import com.example.trip.ui.RegisterScreen
import com.example.trip.ui.theme.TripTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

@Serializable
data object Login : NavKey

@Serializable
data object Register : NavKey

@Serializable
data object ForgotPassword : NavKey

@Serializable
data class Home(val email: String, val password: String) : NavKey

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(Login)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key: NavKey ->
            when (key) {
                is Login -> NavEntry(key) {
                    LoginScreen(
                        onNavigateToRegister = { backStack.add(Register) },
                        onNavigateToForgotPassword = { backStack.add(ForgotPassword) },
                        onLogin = { email: String, password: String -> backStack.add(Home(email, password)) }
                    )
                }

                is Register -> NavEntry(key) {
                    RegisterScreen({ backStack.add(Login) })
                }

                is ForgotPassword -> NavEntry(key) {
                    ForgotPasswordScreen({ backStack.add(Login) })
                }

                is Home -> NavEntry(key) {
                    HomeScreen(
                        email = key.email,
                        password = key.password,
                        onSignOut = { backStack.removeLastOrNull() }
                    )
                }

                else -> NavEntry(Login) { Text("Unknown route") }
            }
        }
    )
}