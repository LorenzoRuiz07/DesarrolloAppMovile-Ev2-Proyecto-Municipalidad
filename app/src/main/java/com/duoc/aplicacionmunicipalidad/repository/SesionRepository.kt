package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.model.Sesion
import com.duoc.aplicacionmunicipalidad.model.SesionDao

class SesionRepository(private val dao: SesionDao) {

    suspend fun obtenerSesion(): Sesion? = dao.obtener()

    suspend fun guardar(perfil: String, usuarioRut: String? = null, fotoPerfilPath: String? = null) =
        dao.guardar(Sesion(perfil = perfil, usuarioRut = usuarioRut, fotoPerfilPath = fotoPerfilPath))

    /** Cambia solo la foto de perfil de la sesión actual. */
    suspend fun guardarFoto(fotoPerfilPath: String?) {
        val sesion = dao.obtener() ?: return
        dao.guardar(sesion.copy(fotoPerfilPath = fotoPerfilPath))
    }

    suspend fun borrar() = dao.borrar()
}
