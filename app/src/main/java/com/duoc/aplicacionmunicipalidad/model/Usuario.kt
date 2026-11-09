package com.duoc.aplicacionmunicipalidad.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey val rut: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val password: String,
    val tipoPerfil: String = "Residente",
    val fotoPerfilPath: String? = null
) {
    fun toPerfilUsuario(): PerfilUsuario = PerfilUsuario(
        nombre = nombre,
        rut = rut,
        correo = correo,
        telefono = telefono,
        direccion = direccion,
        tipoPerfil = tipoPerfil,
        fotoPerfil = fotoPerfilPath
    )
}
