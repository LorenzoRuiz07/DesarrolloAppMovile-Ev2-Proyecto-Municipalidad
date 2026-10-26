package com.duoc.aplicacionmunicipalidad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.aplicacionmunicipalidad.ui.theme.AplicacionMuniSanJoaquinTheme
import com.duoc.aplicacionmunicipalidad.view.HomeScreen
import com.duoc.aplicacionmunicipalidad.view.LoginScreen
import com.duoc.aplicacionmunicipalidad.view.ProfileScreen
import com.duoc.aplicacionmunicipalidad.view.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionMuniSanJoaquinTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = { perfil ->
                                    println("¡Login exitoso! Bienvenido perfil: $perfil")
                                    // Navega a home y borra login del historial para no volver al presionar 'Atrás'
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onRegisterClick = { navController.navigate("registro") }
                            )
                        }
                        composable("registro") {
                            RegisterScreen(
                                onRegisterSuccess = { navController.popBackStack() },
                                onBackToLoginClick = { navController.popBackStack() }
                            )
                        }
                        composable("home") {
                            HomeScreen()
                        }
                        composable("perfil") {
                            ProfileScreen(
                                onBackClick = { navController.popBackStack() },
                                onLogout = {
                                    // Vuelve al login y borra home y perfil del historial
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
