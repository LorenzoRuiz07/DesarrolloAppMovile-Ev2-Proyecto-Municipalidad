package com.duoc.aplicacionmunicipalidad.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aplicacionmunicipalidad.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val perfil = state.perfil


    val primaryGreen = Color(0xFF00796B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Barra superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Mi perfil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Avatar con iniciales
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(primaryGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = perfil.iniciales,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = perfil.nombre,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Text(
            text = "Municipalidad de San Joaquín",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
        )

        //Etiqueta con el tipo de perfil
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = primaryGreen.copy(alpha = 0.15f)
        ) {
            Text(
                text = perfil.tipoPerfil,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryGreen,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        //Datos personales
        TarjetaPerfil(titulo = "Datos personales") {
            FilaDato(Icons.Filled.AccountBox, "RUT", perfil.rut, primaryGreen)
            Separador()
            FilaDato(Icons.Filled.Email, "Correo electrónico", perfil.correo, primaryGreen)
            Separador()
            FilaDato(Icons.Filled.Phone, "Teléfono", perfil.telefono, primaryGreen)
            Separador()
            FilaDato(Icons.Filled.Home, "Dirección o sector", perfil.direccion, primaryGreen)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Preferencias
        TarjetaPerfil(titulo = "Preferencias") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = primaryGreen
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Notificaciones",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = "Recibe avisos de tu comuna",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
                Switch(
                    checked = state.notificaciones,
                    onCheckedChange = viewModel::onNotificacionesChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = primaryGreen
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        //Botones
        Button(
            onClick = onEditClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
        ) {
            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Editar perfil",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = viewModel::onSalirClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(1.dp, Color.Red),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrar sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    //Confirmación antes de cerrar sesión
    if (state.mostrarDialogoSalir) {
        AlertDialog(
            onDismissRequest = viewModel::onDialogoDismiss,
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres salir de tu cuenta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDialogoDismiss()
                        onLogout()
                    }
                ) {
                    Text("Cerrar sesión", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDialogoDismiss) {
                    Text("Cancelar", color = primaryGreen)
                }
            }
        )
    }
}

@Composable
private fun TarjetaPerfil(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFCBD5D0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            contenido()
        }
    }
}

@Composable
private fun FilaDato(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    colorIcono: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icono, contentDescription = null, tint = colorIcono)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = etiqueta, fontSize = 13.sp, color = Color.Gray)
            Text(
                text = if (valor.isBlank()) "No registrado" else valor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun Separador() {
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE3EAE6))
    )
}
