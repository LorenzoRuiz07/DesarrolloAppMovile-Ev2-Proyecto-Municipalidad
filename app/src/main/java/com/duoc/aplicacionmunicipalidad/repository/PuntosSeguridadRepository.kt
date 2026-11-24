package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.model.PuntoSeguridad
import com.duoc.aplicacionmunicipalidad.remote.PuntoSeguridadRespuesta
import com.duoc.aplicacionmunicipalidad.remote.XanoApi
import com.duoc.aplicacionmunicipalidad.remote.XanoClient

/** Obtiene desde la API de Xano los puntos de seguridad de la comuna. */
class PuntosSeguridadRepository(
    private val api: XanoApi = XanoClient.api
) {

    suspend fun obtenerPuntos(): List<PuntoSeguridad> =
        api.obtenerPuntosSeguridad()
            .mapNotNull { aPuntoSeguridad(it) }
            .sortedWith(compareBy({ it.tipo }, { it.nombre }))

    // Descarta los registros que no tienen ubicación, porque no se podrían mostrar en el mapa
    private fun aPuntoSeguridad(respuesta: PuntoSeguridadRespuesta): PuntoSeguridad? {
        val latitud = respuesta.latitud ?: return null
        val longitud = respuesta.longitud ?: return null

        return PuntoSeguridad(
            id = respuesta.id ?: 0L,
            nombre = respuesta.nombre.orEmpty().ifBlank { "Punto sin nombre" },
            tipo = respuesta.tipo.orEmpty().ifBlank { "Otros" },
            direccion = respuesta.direccion.orEmpty().ifBlank { "Dirección no registrada" },
            latitud = latitud,
            longitud = longitud
        )
    }
}
