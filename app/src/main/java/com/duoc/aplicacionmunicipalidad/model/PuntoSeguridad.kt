package com.duoc.aplicacionmunicipalidad.model

/** Lugar de ayuda cercano: Carabineros, Bomberos o un centro de salud. */
data class PuntoSeguridad(
    val id: Long,
    val nombre: String,
    val tipo: String,          // Carabineros, Bomberos o Salud
    val direccion: String,
    val latitud: Double,
    val longitud: Double
)

val TIPOS_PUNTO_SEGURIDAD = listOf("Carabineros", "Bomberos", "Salud")
