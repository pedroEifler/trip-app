package com.example.trip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.trip.data.repository.UserRepository
import com.example.trip.ui.AboutScreen
import com.example.trip.ui.ForgotPasswordScreen
import com.example.trip.ui.HomeScreen
import com.example.trip.ui.LoginScreen
import com.example.trip.ui.MyTripsScreen
import com.example.trip.ui.NewTripScreen
import com.example.trip.ui.RegisterScreen
import com.example.trip.ui.theme.TripTheme
import com.example.trip.viewmodel.ForgotPasswordViewModel
import com.example.trip.viewmodel.LoginViewModel
import com.example.trip.viewmodel.RegisterViewModel
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(onExitApp = { finish() })
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

@Serializable
data object NewTrip : NavKey

@Serializable
data object MyTrips : NavKey

@Serializable
data object About : NavKey

@Composable
fun AppNavigation(onExitApp: () -> Unit) {
    val backStack = rememberNavBackStack(Login)
    val context = LocalContext.current
    val app = context.applicationContext as TripApplication
    val userRepository: UserRepository = app.userRepository

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key: NavKey ->
            when (key) {
                is Login -> NavEntry(key) {
                    val loginVm: LoginViewModel = viewModel(
                        factory = LoginViewModel.provideFactory(userRepository)
                    )
                    LoginScreen(
                        onNavigateToRegister = { backStack.add(Register) },
                        onNavigateToForgotPassword = { backStack.add(ForgotPassword) },
                        onLogin = { email: String, password: String -> backStack.add(Home(email, password)) },
                        vm = loginVm
                    )
                }

                is Register -> NavEntry(key) {
                    val registerVm: RegisterViewModel = viewModel(
                        factory = RegisterViewModel.provideFactory(userRepository)
                    )
                    RegisterScreen(
                        onNavigateBack = { backStack.add(Login) },
                        vm = registerVm
                    )
                }

                is ForgotPassword -> NavEntry(key) {
                    val forgotVm: ForgotPasswordViewModel = viewModel(
                        factory = ForgotPasswordViewModel.provideFactory(userRepository)
                    )
                    ForgotPasswordScreen(
                        onNavigateBack = { backStack.add(Login) },
                        vm = forgotVm
                    )
                }

                is Home -> NavEntry(key) {
                    // BackHandler para encerrar o app quando estiver na Home e pressionar voltar
                    BackHandler {
                        onExitApp()
                    }
                    HomeScreen(
                        email = key.email,
                        onSignOut = { backStack.removeLastOrNull() },
                        onNavigateToNewTrip = { backStack.add(NewTrip) },
                        onNavigateToMyTrips = { backStack.add(MyTrips) },
                        onNavigateToAbout = { backStack.add(About) }
                    )
                }

                is NewTrip -> NavEntry(key) {
                    NewTripScreen(
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                is MyTrips -> NavEntry(key) {
                    MyTripsScreen(
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                is About -> NavEntry(key) {
                    AboutScreen(
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                else -> NavEntry(Login) { Text("Unknown route") }
            }
        }
    )
}