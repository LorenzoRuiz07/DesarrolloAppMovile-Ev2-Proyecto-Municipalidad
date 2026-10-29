package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.model.Sesion
import com.duoc.aplicacionmunicipalidad.model.SesionDao

class SesionRepository(private val dao: SesionDao) {


    suspend fun obtenerPerfil(): String? = dao.obtener()?.perfil

    suspend fun guardar(perfil: String) = dao.guardar(Sesion(perfil = perfil))

    suspend fun borrar() = dao.borrar()
}