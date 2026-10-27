package com.duoc.aplicacionmunicipalidad.view


import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight


@Composable
fun BarraNavegacion(
    seleccionado: String,
    onInicioClick: () -> Unit = {},
    onCasosClick: () -> Unit = {},
    onAyudaClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    NavigationBar(containerColor = Color.White) {
        ItemBarra("Inicio", Icons.Outlined.Home, seleccionado == "inicio", onInicioClick)
        ItemBarra("Mis casos", Icons.Outlined.Assignment, seleccionado == "casos", onCasosClick)
        ItemBarra("Ayuda", Icons.Outlined.HelpOutline, seleccionado == "ayuda", onAyudaClick)
        ItemBarra("Perfil", Icons.Outlined.PersonOutline, seleccionado == "perfil", onPerfilClick)
    }
}

@Composable
private fun RowScope.ItemBarra(
    texto: String,
    icono: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = seleccionado,
        onClick = onClick,
        icon = { Icon(icono, contentDescription = texto) },
        label = {
            Text(
                text = texto,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
