package com.duoc.aplicacionmunicipalidad.view


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.duoc.aplicacionmunicipalidad.model.CATEGORIAS
import com.duoc.aplicacionmunicipalidad.model.tiposDe
import com.duoc.aplicacionmunicipalidad.viewmodel.Evidencia
import com.duoc.aplicacionmunicipalidad.viewmodel.NuevoRequerimientoViewModel

@Composable
fun NuevoRequerimientoScreen(
    viewModel: NuevoRequerimientoViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onEnviado: () -> Unit = {}           // Se ejecuta cuando el usuario cierra el aviso de éxito
) {
    val state by viewModel.uiState.collectAsState()


    var mostrarFecha by remember { mutableStateOf(false) }
    var mostrarHora by remember { mutableStateOf(false) }
    var mostrarAyuda by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Nuevo requerimiento",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { mostrarAyuda = true }) {
                Icon(Icons.Outlined.HelpOutline, contentDescription = "Ayuda")
            }
        }

        //formulario
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            //perfil y estado del borrador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Perfil residente",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Text(
                    text = "Borrador · sin enviar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            //encabezado
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Cuéntanos qué ocurre",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Entrega una referencia clara para facilitar la revisión y asignación de tu caso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Campos obligatorios, salvo evidencia.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // --- Situación ---
            Seccion("Situación") {
                CampoSeleccion(
                    etiqueta = "Categoría",
                    valor = state.categoria,
                    opciones = CATEGORIAS,
                    onSeleccion = viewModel::onCategoriaChange
                )
                CampoSeleccion(
                    etiqueta = "Tipo de situación",
                    valor = state.tipo,
                    opciones = tiposDe(state.categoria),   // vacío hasta elegir categoría
                    onSeleccion = viewModel::onTipoChange
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CampoClicable(
                        etiqueta = "Fecha",
                        valor = state.fechaTexto,
                        icono = Icons.Outlined.CalendarToday,
                        onClick = { mostrarFecha = true },
                        modifier = Modifier.weight(1f)
                    )
                    CampoClicable(
                        etiqueta = "Hora",
                        valor = state.horaTexto,
                        icono = Icons.Outlined.AccessTime,
                        onClick = { mostrarHora = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // --- ¿Dónde ocurre? ---
            Seccion("¿Dónde ocurre?") {
                OutlinedTextField(
                    value = state.lugar,
                    onValueChange = viewModel::onLugarChange,
                    label = { Text("Lugar o sector referencial") },
                    trailingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = viewModel::onUsarUbicacion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Outlined.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Usar mi ubicación")
                }
                Text(
                    text = "Puedes indicar una calle, cruce o punto de referencia. No incluyas tu domicilio personal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // --- Prioridad ---
            Seccion("Prioridad") {
                SelectorPrioridad(state.prioridad, viewModel::onPrioridadChange)
                Text(
                    text = descripcionPrioridad(state.prioridad),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // --- Descripción ---
            OutlinedTextField(
                value = state.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción breve") },
                minLines = 4,
                shape = RoundedCornerShape(12.dp),
                supportingText = {
                    Text(
                        text = "${state.descripcion.length} / 500",
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // --- Evidencia ---
            Seccion("Evidencia") {
                Text(
                    text = "Opcional · evita rostros y datos personales",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(
                    onClick = viewModel::onAdjuntarEvidencia,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Outlined.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Adjuntar con cámara")
                }
                if (state.evidencia != null) {
                    EvidenciaItem(state.evidencia!!, viewModel::onQuitarEvidencia)
                }
                Text(
                    text = "Captura simulada · no activa la cámara.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Aviso de emergencia
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Este registro no es un canal de emergencia. Si existe riesgo inmediato, utiliza los canales de emergencia.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Mensaje de error
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botón enviar
            Button(
                onClick = viewModel::enviar,
                enabled = !state.enviando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = CircleShape
            ) {
                if (state.enviando) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Enviar requerimiento", fontWeight = FontWeight.Medium)
                }
            }

            Text(
                text = "Al enviarlo recibirás un identificador para consultar el estado y sus actualizaciones.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // ---------------- Diálogos ----------------

    if (mostrarFecha) {
        DialogoFecha(
            onConfirmar = viewModel::onFechaSeleccionada,
            onCerrar = { mostrarFecha = false }
        )
    }

    if (mostrarHora) {
        DialogoHora(
            horaInicial = state.hora,
            minutoInicial = state.minuto,
            onConfirmar = viewModel::onHoraSeleccionada,
            onCerrar = { mostrarHora = false }
        )
    }

    if (mostrarAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarAyuda = false },
            title = { Text("Ayuda") },
            text = {
                Text(
                    "Completa los campos obligatorios y describe lo que ocurre sin incluir datos " +
                            "personales. Tu requerimiento será revisado y asignado por el equipo municipal."
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarAyuda = false }) { Text("Entendido") }
            }
        )
    }

    // Aviso de envío exitoso
    if (state.idEnviado != null) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Requerimiento enviado") },
            text = {
                Text(
                    "Tu identificador es ${state.idEnviado}. Úsalo para consultar el estado " +
                            "y las actualizaciones de tu caso."
                )
            },
            confirmButton = {
                TextButton(onClick = onEnviado) { Text("Aceptar", fontWeight = FontWeight.Bold) }
            }
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Componentes del formulario
// ---------------------------------------------------------------------------------------------

// Sección con título
@Composable
private fun Seccion(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        contenido()
    }
}

// Campo que no se escribe: al tocarlo ejecuta una acción (abrir menú, calendario, reloj)
@Composable
private fun CampoClicable(
    etiqueta: String,
    valor: String,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { Icon(icono, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente que recibe el toque
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
    }
}

// Campo con menú desplegable
@Composable
private fun CampoSeleccion(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        CampoClicable(
            etiqueta = etiqueta,
            valor = valor,
            icono = Icons.Outlined.KeyboardArrowDown,
            onClick = { if (opciones.isNotEmpty()) abierto = true },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        abierto = false
                    }
                )
            }
        }
    }
}

// Selector Baja | Media | Alta
@Composable
private fun SelectorPrioridad(seleccion: String, onChange: (String) -> Unit) {
    val opciones = listOf("Baja", "Media", "Alta")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
    ) {
        opciones.forEach { opcion ->
            val activo = opcion == seleccion
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (activo) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                    )
                    .clickable { onChange(opcion) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (activo) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(
                        text = opcion,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun descripcionPrioridad(prioridad: String): String = when (prioridad) {
    "Baja" -> "Baja: no afecta el uso del espacio ni la seguridad. La prioridad será validada por el equipo."
    "Alta" -> "Alta: existe riesgo para las personas o el entorno. La prioridad será validada por el equipo."
    else -> "Media: afecta el uso del espacio, sin riesgo inmediato. La prioridad será validada por el equipo."
}

// Tarjeta de la evidencia adjunta
@Composable
private fun EvidenciaItem(evidencia: Evidencia, onQuitar: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Evidencia fotográfica",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = evidencia.nombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = evidencia.detalle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onQuitar) {
                Icon(Icons.Filled.Close, contentDescription = "Quitar evidencia")
            }
        }
    }
}

// Calendario para elegir la fecha
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoFecha(
    onConfirmar: (Long) -> Unit,
    onCerrar: () -> Unit
) {
    val pickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let { onConfirmar(it) }
                    onCerrar()
                }
            ) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    ) {
        DatePicker(state = pickerState)
    }
}

// Reloj para elegir la hora
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoHora(
    horaInicial: Int,
    minutoInicial: Int,
    onConfirmar: (Int, Int) -> Unit,
    onCerrar: () -> Unit
) {
    val pickerState = rememberTimePickerState(
        initialHour = horaInicial,
        initialMinute = minutoInicial,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Selecciona la hora") },
        text = { TimePicker(state = pickerState) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmar(pickerState.hour, pickerState.minute)
                    onCerrar()
                }
            ) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    )
}
