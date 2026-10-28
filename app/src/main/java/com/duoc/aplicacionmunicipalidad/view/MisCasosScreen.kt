package com.duoc.aplicacionmunicipalidad.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.viewmodel.MisCasosViewModel

@Composable
fun MisCasosScreen(
    viewModel: MisCasosViewModel = viewModel(),
    onRequerimientoClick: (Requerimiento) -> Unit = {},
    onInicioClick: () -> Unit = {},
    onAyudaClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val visibles = state.visibles

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BarraNavegacion(
                seleccionado = "casos",
                onInicioClick = onInicioClick,
                onAyudaClick = onAyudaClick,
                onPerfilClick = onPerfilClick
            )
        }
    ) { paddingScaffold ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingScaffold),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (state.esFuncionario) "Casos a gestionar" else "Mis casos",
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (state.esFuncionario) {
                            "Revisa y gestiona los requerimientos de la comuna."
                        } else {
                            "Consulta el avance de los requerimientos que registraste."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Todos", "En curso", "Resueltos").forEach { opcion ->
                        ChipFiltro(
                            texto = opcion,
                            seleccionado = state.filtro == opcion,
                            onClick = { viewModel.onFiltroChange(opcion) }
                        )
                    }
                }
            }

            item {
                val n = visibles.size
                Text(
                    text = if (n == 1) "1 requerimiento" else "$n requerimientos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (visibles.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay requerimientos en esta lista.",
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

@Composable
private fun ChipFiltro(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}