package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.remote.GestionSolicitud
import com.duoc.aplicacionmunicipalidad.remote.MunicipalidadApi
import com.duoc.aplicacionmunicipalidad.remote.NuevoRequerimientoSolicitud
import com.duoc.aplicacionmunicipalidad.remote.RetrofitClient

/** Accede a los requerimientos que guarda el microservicio. Lanza excepciones si la red falla. */
class RequerimientoRepository(
    private val api: MunicipalidadApi = RetrofitClient.api
) {

    /** Con [rutUsuario] devuelve solo los casos de ese vecino; sin él, todos los casos. */
    suspend fun listar(rutUsuario: String? = null): List<Requerimiento> =
        api.listarRequerimientos(rutUsuario).map { it.aRequerimiento() }

    suspend fun obtener(id: String): Requerimiento =
        api.obtenerRequerimiento(id).aRequerimiento()

    suspend fun crear(solicitud: NuevoRequerimientoSolicitud): Requerimiento =
        api.crearRequerimiento(solicitud).aRequerimiento()

    suspend fun gestionar(id: String, solicitud: GestionSolicitud): Requerimiento =
        api.gestionarRequerimiento(id, solicitud).aRequerimiento()

    suspend fun eliminar(id: String) = api.eliminarRequerimiento(id)
}
