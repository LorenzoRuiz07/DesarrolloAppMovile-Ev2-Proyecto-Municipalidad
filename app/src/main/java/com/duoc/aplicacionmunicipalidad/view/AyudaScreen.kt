package com.duoc.aplicacionmunicipalidad.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AyudaScreen(
    onInicioClick: () -> Unit = {},
    onCasosClick: () -> Unit = {},
    onAyudaClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            BarraNavegacion(
                seleccionado = "ayuda",
                onInicioClick = onInicioClick,
                onCasosClick = onCasosClick,
                onAyudaClick = onAyudaClick,
                onPerfilClick = onPerfilClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Centro de Ayuda",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Guía de uso rápido
            SeccionAyuda(
                titulo = "Guía de uso rápido",
                icono = Icons.AutoMirrored.Outlined.Assignment,
                colorIcono = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "• Registrar un caso: Ve a Inicio y presiona 'Nuevo Requerimiento'. Completa el formulario con la situación, ubicación y opcionalmente adjunta una foto.\n\n" +
                            "• Consultar un caso: Ve a la pestaña 'Mis casos' para revisar el estado (Pendiente, En revisión, Resuelto) de los requerimientos que has enviado."
                )
            }

            // Explicación de prioridades
            SeccionAyuda(
                titulo = "Explicación de prioridades",
                icono = Icons.Outlined.PriorityHigh,
                colorIcono = Color(0xFFF57C00)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrioridadItem(
                        nivel = "Prioridad Baja",
                        descripcion = "No afecta el uso del espacio ni la seguridad inmediata. Ejemplo: Basurero lleno, pintura de banca desgastada."
                    )
                    PrioridadItem(
                        nivel = "Prioridad Media",
                        descripcion = "Afecta el uso del espacio pero sin riesgo inminente. Ejemplo: Luminaria apagada, juego infantil dañado."
                    )
                    PrioridadItem(
                        nivel = "Prioridad Alta",
                        descripcion = "Existe riesgo para las personas o el entorno. Ejemplo: Ramas a punto de caer sobre el paso, cableado expuesto."
                    )
                }
            }

            // Aviso de responsabilidad
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Aviso importante",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Aviso de responsabilidad: Esta plataforma es únicamente para registro y seguimiento. Ante riesgos inmediatos que amenacen la vida o seguridad, utiliza los canales de emergencia oficiales (133 Carabineros, 132 Bomberos, 131 Ambulancia).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SeccionAyuda(
    titulo: String,
    icono: ImageVector,
    colorIcono: Color,
    contenido: @Composable () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                contenido()
            }
        }
    }
}

@Composable
private fun PrioridadItem(nivel: String, descripcion: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = nivel,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 24.dp, top = 2.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
