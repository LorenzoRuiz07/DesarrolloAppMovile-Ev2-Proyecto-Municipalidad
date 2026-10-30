package com.duoc.aplicacionmunicipalidad.model

object SesionUsuario {
    var perfil: String = "Residente"   // Residente, Operador, Supervisor o Administrador

    val esFuncionario: Boolean
        get() = perfil != "Residente"

    // Datos globales para persistir en memoria el perfil del usuario actual
    var perfilUsuarioActual: PerfilUsuario? = null
}