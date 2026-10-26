package com.duoc.aplicacionmunicipalidad.model


data class PerfilUsuario(
    val nombre: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val tipoPerfil: String
) {
    //Iniciales para el avatar
    val iniciales: String
        get() = nombre.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
}
