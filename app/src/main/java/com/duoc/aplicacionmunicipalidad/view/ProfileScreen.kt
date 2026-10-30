package com.duoc.aplicacionmunicipalidad.view

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.duoc.aplicacionmunicipalidad.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val perfil = state.perfil
    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var mostrarOpcionesFoto by remember { mutableStateOf(false) }
    var fotoTemporal by remember { mutableStateOf<Any?>(null) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val primaryGreen = Color(0xFF00796B)

    // Lanzador para la cámara (TakePicturePreview devuelve un Bitmap)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoTemporal = bitmap
            mostrarConfirmacion = true
        }
    }

    // Lanzador para la galería (PickVisualMedia devuelve una Uri)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            fotoTemporal = uri
            mostrarConfirmacion = true
        }
    }

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

        //Avatar con iniciales o foto
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(primaryGreen)
                .clickable { mostrarOpcionesFoto = true },
            contentAlignment = Alignment.Center
        ) {
            if (perfil.fotoPerfil != null) {
                AsyncImage(
                    model = perfil.fotoPerfil,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = perfil.iniciales,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Ícono de cámara pequeño en la esquina inferior derecha
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(8.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = primaryGreen,
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxSize()
                    )
                }
            }
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
            onClick = { mostrarDialogoEditar = true },
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

    if (mostrarDialogoEditar) {
        DialogoEditarPerfil(
            perfilInicial = perfil,
            onDismiss = { mostrarDialogoEditar = false },
            onGuardar = { nombre, rut, correo, telefono, direccion ->
                viewModel.onGuardarPerfil(nombre, rut, correo, telefono, direccion)
                mostrarDialogoEditar = false
            }
        )
    }
    
    if (mostrarOpcionesFoto) {
        AlertDialog(
            onDismissRequest = { mostrarOpcionesFoto = false },
            title = { Text("Cambiar foto de perfil") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Tomar foto con la cámara",
                        color = primaryGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                mostrarOpcionesFoto = false
                                cameraLauncher.launch(null)
                            }
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Elegir de la galería",
                        color = primaryGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                mostrarOpcionesFoto = false
                                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                            .padding(vertical = 8.dp)
                    )
                    if (perfil.fotoPerfil != null) {
                        Text(
                            text = "Eliminar foto actual",
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    mostrarOpcionesFoto = false
                                    viewModel.onActualizarFoto(null)
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarOpcionesFoto = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Confirmar foto") },
            text = { Text("¿Deseas guardar esta foto como tu foto de perfil?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onActualizarFoto(fotoTemporal)
                        mostrarConfirmacion = false
                    }
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold, color = primaryGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun DialogoEditarPerfil(
    perfilInicial: com.duoc.aplicacionmunicipalidad.model.PerfilUsuario,
    onDismiss: () -> Unit,
    onGuardar: (String, String, String, String, String) -> Unit
) {
    var nombre by remember { mutableStateOf(perfilInicial.nombre) }
    var rut by remember { mutableStateOf(perfilInicial.rut) }
    var correo by remember { mutableStateOf(perfilInicial.correo) }
    var telefono by remember { mutableStateOf(perfilInicial.telefono) }
    var direccion by remember { mutableStateOf(perfilInicial.direccion) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Perfil") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rut,
                    onValueChange = { rut = it },
                    label = { Text("RUT") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección o sector") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onGuardar(nombre, rut, correo, telefono, direccion)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
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
