package com.duoc.aplicacionmunicipalidad.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aplicacionmunicipalidad.model.PuntoSeguridad
import com.duoc.aplicacionmunicipalidad.model.TIPOS_PUNTO_SEGURIDAD
import com.duoc.aplicacionmunicipalidad.viewmodel.PuntosSeguridadViewModel

@Composable
fun PuntosSeguridadScreen(
    viewModel: PuntosSeguridadViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) { viewModel.cargar() }

    val state by viewModel.uiState.collectAsState()
    val visibles = state.visibles

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
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
                text = "Puntos de seguridad",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "Carabineros, bomberos y centros de salud cerca de San Joaquín. " +
                    "Datos entregados por la API de la municipalidad.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = state.filtroTipo == null,
                    onClick = { viewModel.onFiltroTipoChange(null) },
                    label = { Text("Todos") }
                )
            }
            items(TIPOS_PUNTO_SEGURIDAD) { tipo ->
                FilterChip(
                    selected = state.filtroTipo == tipo,
                    onClick = { viewModel.onFiltroTipoChange(tipo) },
                    label = { Text(tipo) }
                )
            }
        }

        when {
            state.cargando -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.error != null -> Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = viewModel::cargar) { Text("Reintentar") }
            }

            visibles.isEmpty() -> Text(
                text = "No encontramos puntos de este tipo cerca.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(20.dp)
            )

            else -> LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(visibles, key = { it.id }) { punto -> TarjetaPunto(punto) }
            }
        }
    }
}

@Composable
private fun TarjetaPunto(punto: PuntoSeguridad) {
    val contexto = LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = punto.tipo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = punto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = punto.direccion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                onClick = {
                    // Abre la aplicación de mapas del teléfono con el lugar marcado
                    val ubicacion = Uri.parse(
                        "geo:${punto.latitud},${punto.longitud}" +
                                "?q=${punto.latitud},${punto.longitud}(${Uri.encode(punto.nombre)})"
                    )
                    try {
                        contexto.startActivity(Intent(Intent.ACTION_VIEW, ubicacion))
                    } catch (error: ActivityNotFoundException) {
                        Toast.makeText(contexto, "No hay una app de mapas instalada.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Cómo llegar")
            }
        }
    }
}
