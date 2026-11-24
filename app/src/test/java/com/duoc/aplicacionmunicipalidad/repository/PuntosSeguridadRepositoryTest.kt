package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.remote.PuntoSeguridadRespuesta
import com.duoc.aplicacionmunicipalidad.remote.XanoApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PuntosSeguridadRepositoryTest {

    private val api = mockk<XanoApi>()
    private val repositorio = PuntosSeguridadRepository(api)

    @Test
    fun `descarta los puntos que no tienen ubicacion`() = runTest {
        coEvery { api.obtenerPuntosSeguridad() } returns listOf(
            PuntoSeguridadRespuesta(1, "Comisaría", "Carabineros", "Calle 1", -33.48, -70.63),
            PuntoSeguridadRespuesta(2, "Sin ubicación", "Bomberos", "Calle 2", null, null)
        )

        val puntos = repositorio.obtenerPuntos()

        assertEquals(1, puntos.size)
        assertEquals("Comisaría", puntos[0].nombre)
    }

    @Test
    fun `completa los textos vacios con valores por defecto`() = runTest {
        coEvery { api.obtenerPuntosSeguridad() } returns listOf(
            PuntoSeguridadRespuesta(null, " ", null, null, -33.5, -70.6)
        )

        val punto = repositorio.obtenerPuntos().single()

        assertEquals(0L, punto.id)
        assertEquals("Punto sin nombre", punto.nombre)
        assertEquals("Otros", punto.tipo)
        assertEquals("Dirección no registrada", punto.direccion)
    }

    @Test
    fun `ordena por tipo y luego por nombre`() = runTest {
        coEvery { api.obtenerPuntosSeguridad() } returns listOf(
            PuntoSeguridadRespuesta(1, "CESFAM B", "Salud", "x", -33.0, -70.0),
            PuntoSeguridadRespuesta(2, "Bomberos 5", "Bomberos", "x", -33.0, -70.0),
            PuntoSeguridadRespuesta(3, "CESFAM A", "Salud", "x", -33.0, -70.0)
        )

        val nombres = repositorio.obtenerPuntos().map { it.nombre }

        assertEquals(listOf("Bomberos 5", "CESFAM A", "CESFAM B"), nombres)
    }
}
