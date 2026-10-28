package com.duoc.aplicacionmunicipalidad.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.ui.text.style.TextAlign
import com.duoc.aplicacionmunicipalidad.model.CATEGORIAS
import com.duoc.aplicacionmunicipalidad.model.ESTADOS
import com.duoc.aplicacionmunicipalidad.model.PRIORIDADES
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import com.duoc.aplicacionmunicipalidad.ui.theme.MapaCalle
import com.duoc.aplicacionmunicipalidad.ui.theme.MapaFondo
import com.duoc.aplicacionmunicipalidad.ui.theme.MapaZona
import com.duoc.aplicacionmunicipalidad.viewmodel.HomeUiState
import com.duoc.aplicacionmunicipalidad.viewmodel.HomeViewModel

//Pantalla principal
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onRequerimientoClick: (Requerimiento) -> Unit = {},
    onNotificacionesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onNuevoRequerimientoClick: () -> Unit = {},
    onMisCasosClick: () -> Unit = {},
    onAyudaClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    HomeContent(
        state = state,
        onConsultaChange = viewModel::onConsultaChange,
        onVerMapaChange = viewModel::onVerMapaChange,
        onEstadoChange = viewModel::onEstadoChange,
        onCategoriaChange = viewModel::onCategoriaChange,
        onPrioridadChange = viewModel::onPrioridadChange,
        onRequerimientoClick = onRequerimientoClick,
        onNotificacionesClick = onNotificacionesClick,
        onPerfilClick = onPerfilClick,
        onNuevoRequerimientoClick = onNuevoRequerimientoClick,
        onMisCasosClick = onMisCasosClick,
        onAyudaClick = onAyudaClick
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onConsultaChange: (String) -> Unit,
    onVerMapaChange: (Boolean) -> Unit,
    onEstadoChange: (String?) -> Unit,
    onCategoriaChange: (String?) -> Unit,
    onPrioridadChange: (String?) -> Unit,
    onRequerimientoClick: (Requerimiento) -> Unit,
    onNotificacionesClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onNuevoRequerimientoClick: () -> Unit,
    onMisCasosClick: () -> Unit,
    onAyudaClick: () -> Unit
) {
    val visibles = state.visibles

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        // Botón flotante "Nuevo requerimiento"
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoRequerimientoClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = {
                    Text(
                        text = "Nuevo requerimiento",
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        },

        //barra de navegación inferior
        bottomBar = {
            Column {
                Text(
                    text = "Municipalidad de San Joaquín",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                BarraNavegacion(
                    seleccionado = "inicio",
                    onCasosClick = onMisCasosClick,
                    onAyudaClick = onAyudaClick,
                    onPerfilClick = onPerfilClick
                )
            }
        }
    ) { paddingScaffold ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingScaffold),   // ya incluye barra de estado y barra inferior
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, top = 16.dp,
                bottom = 96.dp               // espacio para que el botón flotante no tape la última tarjeta
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Encabezado(onNotificacionesClick) }

            item { TituloResidente(onPerfilClick) }

            item {
                Text(
                    text = "Consulta requerimientos del sector y sigue sus avances.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item { CampoBusqueda(state.consulta, onConsultaChange) }

            item { SelectorVista(state.verMapa, onVerMapaChange) }

            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FiltroChip("Estado", state.filtroEstado, ESTADOS, onEstadoChange)
                    FiltroChip("Categoría", state.filtroCategoria, CATEGORIAS, onCategoriaChange)
                    FiltroChip("Prioridad", state.filtroPrioridad, PRIORIDADES, onPrioridadChange)
                }
            }

            if (state.verMapa) {
                // TODO: reemplazar por GoogleMaps() con marcadores de los requerimientos visibles
                item {
                    MapaReferencial(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
                    )
                }
            } else {
                item {
                    MapaReferencial(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(142.dp),
                        onExpandir = { onVerMapaChange(true) }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Requerimientos del sector",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Más recientes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (visibles.isEmpty()) {
                    item {
                        Text(
                            text = "No hay requerimientos que coincidan con tu búsqueda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(visibles, key = { it.id }) { requerimiento ->
                        RequerimientoCard(
                            requerimiento = requerimiento,
                            destacado = requerimiento.estado == "En atención",
                            onClick = { onRequerimientoClick(requerimiento) }
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Encabezado
// ---------------------------------------------------------------------------------------------

@Composable
private fun Encabezado(onNotificacionesClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Municipalidad de San Joaquín",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Seguridad comunal",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onNotificacionesClick) {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = "Notificaciones",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun TituloResidente(onPerfilClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tu comuna, al día",
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        Surface(
            onClick = onPerfilClick,
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = SesionUsuario.perfil,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Búsqueda, selector Feed/Mapa y filtros
// ---------------------------------------------------------------------------------------------

@Composable
private fun CampoBusqueda(valor: String, onChange: (String) -> Unit) {
    TextField(
        value = valor,
        onValueChange = onChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        singleLine = true,
        shape = CircleShape,
        placeholder = {
            Text(
                text = "Buscar por lugar o identificador",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun SelectorVista(verMapa: Boolean, onVerMapaChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
    ) {
        Segmento(
            texto = "Feed",
            icono = Icons.AutoMirrored.Outlined.ViewList,
            seleccionado = !verMapa,
            onClick = { onVerMapaChange(false) },
            modifier = Modifier.weight(1f)
        )
        Segmento(
            texto = "Mapa",
            icono = Icons.Outlined.Map,
            seleccionado = verMapa,
            onClick = { onVerMapaChange(true) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun Segmento(
    texto: String,
    icono: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                if (seleccionado) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (seleccionado) Icons.Filled.Check else icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FiltroChip(
    titulo: String,
    seleccion: String?,
    opciones: List<String>,
    onSeleccion: (String?) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }

    Box {
        Surface(
            onClick = { abierto = true },
            shape = RoundedCornerShape(10.dp),
            color = if (seleccion != null) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                Color.Transparent
            },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = seleccion ?: titulo,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            DropdownMenuItem(
                text = { Text("Todos") },
                onClick = {
                    onSeleccion(null)
                    abierto = false
                }
            )
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

// ---------------------------------------------------------------------------------------------
// Mapa referencial (ilustración; no requiere API key de Google Maps)
// ---------------------------------------------------------------------------------------------

@Composable
fun MapaReferencial(
    modifier: Modifier = Modifier,
    onExpandir: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MapaFondo)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            // Calles
            drawRect(MapaCalle, Offset(0f, h * 0.10f), Size(w, h * 0.10f))
            drawRect(MapaCalle, Offset(0f, h * 0.80f), Size(w, h * 0.10f))
            drawRect(MapaCalle, Offset(w * 0.23f, 0f), Size(w * 0.04f, h))
            drawRect(MapaCalle, Offset(w * 0.68f, 0f), Size(w * 0.04f, h))
            // Zona destacada (plaza)
            drawRoundRect(
                color = MapaZona,
                topLeft = Offset(w * 0.37f, h * 0.02f),
                size = Size(w * 0.27f, h * 0.70f),
                cornerRadius = CornerRadius(24.dp.toPx())
            )
        }

        //Etiqueta superior izquierda
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White
        ) {
            Text(
                text = "Mapa referencial",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }

        //Pin principal + nombre del lugar
        PinMapa(
            modifier = Modifier.align(BiasAlignment(0f, -0.35f)),
            principal = true
        )
        Text(
            text = "Plaza Valdivieso",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(BiasAlignment(0f, 0.30f))
        )

        //Pines secundarios
        PinMapa(modifier = Modifier.align(BiasAlignment(-0.62f, 0.45f)))
        PinMapa(modifier = Modifier.align(BiasAlignment(0.76f, -0.25f)))

        //Nombre de calle
        Text(
            text = "Carmen",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(BiasAlignment(0.62f, 0.72f))
        )

        //Botón pantalla completa
        Surface(
            onClick = onExpandir,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Fullscreen,
                    contentDescription = "Ver mapa completo",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PinMapa(modifier: Modifier = Modifier, principal: Boolean = false) {
    Box(
        modifier = modifier
            .size(if (principal) 38.dp else 36.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(if (principal) MaterialTheme.colorScheme.primary else Color.White),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = if (principal) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Tarjeta de requerimiento
// ---------------------------------------------------------------------------------------------

@Composable
fun RequerimientoCard(
    requerimiento: Requerimiento,
    destacado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(
            width = 1.dp,
            color = if (destacado) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EstadoChip(requerimiento.estado)
                Text(
                    text = requerimiento.id,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = requerimiento.titulo,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${requerimiento.lugar} · ${requerimiento.sector}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${requerimiento.categoria} · ${requerimiento.prioridad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = requerimiento.fechaFormateada,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EstadoChip(estado: String) {
    val colorFondo = when (estado) {
        "En atención" -> MaterialTheme.colorScheme.primaryContainer
        "Resuelto" -> Color.White
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = colorFondo,
        border = if (estado == "Resuelto") {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Text(
            text = estado,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------------------------

@Preview(showBackground = true, widthDp = 412, heightDp = 860)
@Composable
private fun HomeScreenPreview() {
    BasicoTheme {
        HomeContent(
            state = HomeUiState(
                requerimientos = RequerimientoRepository().obtenerRequerimientos()
            ),
            onConsultaChange = {},
            onVerMapaChange = {},
            onEstadoChange = {},
            onCategoriaChange = {},
            onPrioridadChange = {},
            onRequerimientoClick = {},
            onNotificacionesClick = {},
            onPerfilClick = {},
            onNuevoRequerimientoClick = {},
            onMisCasosClick = {},
            onAyudaClick = {}
        )
    }
}

@Composable
fun BasicoTheme(content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}