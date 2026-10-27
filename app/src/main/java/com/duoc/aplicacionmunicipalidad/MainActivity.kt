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
import com.duoc.aplicacionmunicipalidad.view.NuevoRequerimientoScreen
import com.duoc.aplicacionmunicipalidad.view.ProfileScreen
import com.duoc.aplicacionmunicipalidad.view.RegisterScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.duoc.aplicacionmunicipalidad.view.SeguimientoScreen

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
                            HomeScreen(
                                onRequerimientoClick = { r -> navController.navigate("detalle/${r.id}") },
                                onPerfilClick = { navController.navigate("perfil") },
                                onNuevoRequerimientoClick = { navController.navigate("nuevo")},
                                onMisCasosClick = {},
                                onAyudaClick = {}
                            )
                        }
                        composable("perfil") {
                            ProfileScreen(
                                onBackClick = { navController.popBackStack() },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("nuevo") {
                            NuevoRequerimientoScreen(
                                onBackClick = { navController.popBackStack() },
                                onEnviado = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = "detalle/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            SeguimientoScreen(
                                idRequerimiento = id,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
