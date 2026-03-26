package com.example.trip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.trip.ui.ForgotPasswordScreen
import com.example.trip.ui.HomeScreen
import com.example.trip.ui.LoginScreen
import com.example.trip.ui.RegisterScreen
import com.example.trip.ui.theme.TripTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation()
                }
            }
        }
    }
}

data object Login
data object Register
data object ForgotPassword
data object Home

@Composable
fun AppNavigation() {

    val backStack = remember { mutableStateListOf<Any>(Login) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Login -> NavEntry(key) {
                    LoginScreen({ backStack.add(Register) }, { backStack.add(ForgotPassword) }, {backStack.add(Home)})
                }

                is Register -> NavEntry(key) {
                    RegisterScreen({ backStack.add(Login) })
                }

                is ForgotPassword -> NavEntry(key) {
                    ForgotPasswordScreen({ backStack.add(Login) })
                }

                is Home -> NavEntry(key) {
                    HomeScreen({ backStack.removeLastOrNull() })
                }

                else -> NavEntry(Unit) { Text("Unknown route") }
            }
        }
    )
}