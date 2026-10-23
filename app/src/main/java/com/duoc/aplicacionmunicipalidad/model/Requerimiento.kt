package com.duoc.aplicacionmunicipalidad.model

import java.text.SimpleDateFormat
import java.util.Locale

data class Requerimiento(
    val id: String,
    val titulo: String,
    val lugar: String,
    val sector: String,
    val categoria: String,
    val prioridad: String,
    val estado: String,
    val fechaMillis: Long
) {
    // Como implementar la fecha?***************************************************************
    val fechaFormateada: String
        get() = SimpleDateFormat("d MMM · HH:mm", Locale("es", "CL"))
            .format(fechaMillis)
            .replace(".", "")
}

// Revisar++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
val ESTADOS = listOf("Recibido", "En atención", "Resuelto")
val CATEGORIAS = listOf("Alumbrado", "Seguridad", "Espacio público", "Tránsito")
val PRIORIDADES = listOf("Alta", "Media", "Baja")