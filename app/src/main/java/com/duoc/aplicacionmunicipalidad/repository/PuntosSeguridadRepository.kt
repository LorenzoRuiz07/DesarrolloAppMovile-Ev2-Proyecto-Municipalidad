package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.model.PuntoSeguridad
import com.duoc.aplicacionmunicipalidad.remote.ElementoOsm
import com.duoc.aplicacionmunicipalidad.remote.OverpassApi
import com.duoc.aplicacionmunicipalidad.remote.OverpassClient

/** Busca en OpenStreetMap los puntos de seguridad cercanos al centro de San Joaquín. */
class PuntosSeguridadRepository(
    private val api: OverpassApi = OverpassClient.api
) {

    companion object {
        private const val LATITUD_CENTRO = -33.4975
        private const val LONGITUD_CENTRO = -70.6240
        private const val RADIO_METROS = 2500

        // Comisarías, cuarteles de bomberos, hospitales y consultorios dentro del radio
        val CONSULTA = "[out:json][timeout:25];" +
                "nwr[\"amenity\"~\"police|fire_station|hospital|clinic\"]" +
                "(around:$RADIO_METROS,$LATITUD_CENTRO,$LONGITUD_CENTRO);" +
                "out center 80;"
    }

    suspend fun obtenerPuntos(): List<PuntoSeguridad> =
        api.buscar(CONSULTA).elements.orEmpty()
            .mapNotNull { aPuntoSeguridad(it) }
            .sortedWith(compareBy({ it.tipo }, { it.nombre }))

    // Descarta los elementos sin ubicación o de un tipo que no nos interesa
    private fun aPuntoSeguridad(elemento: ElementoOsm): PuntoSeguridad? {
        val etiquetas = elemento.tags ?: return null
        val tipo = when (etiquetas["amenity"]) {
            "police" -> "Carabineros"
            "fire_station" -> "Bomberos"
            "hospital", "clinic" -> "Salud"
            else -> return null
        }
        val latitud = elemento.lat ?: elemento.center?.lat ?: return null
        val longitud = elemento.lon ?: elemento.center?.lon ?: return null

        val calle = etiquetas["addr:street"].orEmpty()
        val numero = etiquetas["addr:housenumber"].orEmpty()

        return PuntoSeguridad(
            id = elemento.id ?: 0L,
            nombre = etiquetas["name"] ?: "$tipo (sin nombre registrado)",
            tipo = tipo,
            direccion = "$calle $numero".trim().ifBlank { "Dirección no registrada" },
            latitud = latitud,
            longitud = longitud
        )
    }
}
