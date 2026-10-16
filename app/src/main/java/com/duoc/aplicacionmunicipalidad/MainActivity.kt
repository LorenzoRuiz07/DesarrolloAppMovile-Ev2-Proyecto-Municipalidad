package com.duoc.aplicacionmunicipalidad

import com.duoc.aplicacionmunicipalidad.ui.theme.AplicacionMuniSanJoaquinTheme


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.duoc.aplicacionmunicipalidad.ui.theme.AplicacionMuniSanJoaquinTheme // <- Importación del tema
import com.duoc.aplicacionmunicipalidad.view.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Reemplazamos el nombre de ejemplo por el tema real de tu app
            AplicacionMuniSanJoaquinTheme() {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LoginScreen(
                        onLoginSuccess = { perfil ->
                            // Cuando el login sea exitoso, esto se imprimirá en el Logcat de Android Studio
                            println("¡Login exitoso! Bienvenido perfil: $perfil")

                            // Aquí más adelante agregaremos la navegación hacia la pantalla del Feed
                        }
                    )
                }
            }
        }
    }
}