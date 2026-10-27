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
    val fechaMillis: Long,
    val tipo: String = "",
    val descripcion: String = "",
    val evidenciaNombre: String = "",
    val evidenciaDetalle: String = ""
) {

    val fechaFormateada: String
        get() = SimpleDateFormat("d MMM · HH:mm", Locale("es", "CL"))
            .format(fechaMillis)
            .replace(".", "")
}


val ESTADOS = listOf("Recibido", "En atención", "Resuelto")
val CATEGORIAS = listOf("Alumbrado", "Seguridad", "Espacio público", "Tránsito")
val PRIORIDADES = listOf("Alta", "Media", "Baja")


fun tiposDe(categoria: String): List<String> = when (categoria) {
    "Alumbrado" -> listOf("Luminaria apagada", "Luminaria dañada", "Poste en mal estado", "Cableado expuesto")
    "Seguridad" -> listOf("Ruidos molestos", "Riña o disturbios", "Persona sospechosa", "Consumo en vía pública", "Vandalismo")
    "Espacio público" -> listOf("Basura acumulada", "Área verde dañada", "Mobiliario dañado", "Grafiti")
    "Tránsito" -> listOf("Semáforo sin funcionar", "Señalética dañada", "Vehículo mal estacionado", "Hoyo en la calzada")
    else -> emptyList()
}