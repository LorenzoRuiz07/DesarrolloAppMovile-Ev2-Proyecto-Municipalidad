package com.duoc.aplicacionmunicipalidad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.ui.theme.AplicacionMuniSanJoaquinTheme
import com.duoc.aplicacionmunicipalidad.view.GestionScreen
import com.duoc.aplicacionmunicipalidad.view.HomeScreen
import com.duoc.aplicacionmunicipalidad.view.LoginScreen
import com.duoc.aplicacionmunicipalidad.view.MisCasosScreen
import com.duoc.aplicacionmunicipalidad.view.NuevoRequerimientoScreen
import com.duoc.aplicacionmunicipalidad.view.ProfileScreen
import com.duoc.aplicacionmunicipalidad.view.RegisterScreen
import com.duoc.aplicacionmunicipalidad.view.SeguimientoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionMuniSanJoaquinTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    // El residente ve el seguimiento; el funcionario abre la gestión del caso
                    val abrirRequerimiento: (Requerimiento) -> Unit = { r ->
                        val ruta = if (SesionUsuario.esFuncionario) "gestion/${r.id}" else "detalle/${r.id}"
                        navController.navigate(ruta)
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = { perfil ->
                                    SesionUsuario.perfil = perfil
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
                            HomeScreen(
                                onRequerimientoClick = abrirRequerimiento,
                                onPerfilClick = { navController.navigate("perfil") },
                                onNuevoRequerimientoClick = { navController.navigate("nuevo") },
                                onMisCasosClick = { navController.navigate("casos") },
                                onAyudaClick = { /* TODO: pantalla "Ayuda" */ }
                            )
                        }
                        composable("casos") {
                            MisCasosScreen(
                                onRequerimientoClick = abrirRequerimiento,
                                onInicioClick = { navController.popBackStack("home", false) },
                                onAyudaClick = { /* TODO: pantalla "Ayuda" */ },
                                onPerfilClick = { navController.navigate("perfil") }
                            )
                        }
                        composable("perfil") {
                            ProfileScreen(
                                onBackClick = { navController.popBackStack() },
                                onLogout = {
                                    SesionUsuario.perfil = "Residente"
                                    // Vuelve al login y borra las pantallas anteriores del historial
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
                        composable(
                            route = "gestion/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            GestionScreen(
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