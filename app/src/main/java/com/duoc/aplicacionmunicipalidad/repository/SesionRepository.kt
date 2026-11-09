package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.model.Sesion
import com.duoc.aplicacionmunicipalidad.model.SesionDao

class SesionRepository(private val dao: SesionDao) {

    suspend fun obtenerSesion(): Sesion? = dao.obtener()

    suspend fun obtenerPerfil(): String? = dao.obtener()?.perfil

    suspend fun guardar(perfil: String, usuarioRut: String? = null) =
        dao.guardar(Sesion(perfil = perfil, usuarioRut = usuarioRut))

    suspend fun borrar() = dao.borrar()
}
