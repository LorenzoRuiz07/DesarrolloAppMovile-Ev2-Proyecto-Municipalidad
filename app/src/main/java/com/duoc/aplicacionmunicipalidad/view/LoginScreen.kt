package com.duoc.aplicacionmunicipalidad.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aplicacionmunicipalidad.viewmodel.LoginState
import com.duoc.aplicacionmunicipalidad.viewmodel.LoginViewModel
import java.nio.file.WatchEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (String) -> Unit // Función que se ejecuta cuando el login es exitoso
) {
    // Variables para capturar el texto
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observamos el estado del ViewModel
    val loginState by viewModel.loginState.collectAsState()

    // Color verde primario basado en la imagen del proyecto
    val primaryGreen = Color(0xFF00796B)

    // Efecto secundario: Cuando el estado cambia a Success, ejecutamos la navegación
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            // Obtenemos el perfil y llamamos a la función de éxito
            val profile = (loginState as LoginState.Success).userProfile
            onLoginSuccess(profile)

            // Opcional: Reiniciamos el estado del login al salir de esta pantalla
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Títulos

        Text(
            textAlign = TextAlign.Center,
            text = "Municipalidad de San Joaquín",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)

        )
        Text(
            text = "Seguridad comunal",
            fontSize = 20.sp,
            color = Color.Black
        )

        // Campo de Usuario / RUT
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario o RUT") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            // Corrección para Material 3
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryGreen,
                focusedLabelColor = primaryGreen
            ),
            singleLine = true
        )

        // Campo de Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Contraseña") }, // Corrección de ícono
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            // Corrección para Material 3
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryGreen,
                focusedLabelColor = primaryGreen
            ),
            singleLine = true
        )

        // Mensaje de Error (solo se muestra si el estado es Error)
        if (loginState is LoginState.Error) {
            Text(
                text = (loginState as LoginState.Error).message,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Mostrar indicador de carga o botón dependiendo del estado
        if (loginState is LoginState.Loading) {
            CircularProgressIndicator(color = primaryGreen)
        } else {
            Button(
                onClick = { viewModel.performLogin(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                Text(
                    text = "Ingresar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}