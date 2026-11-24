package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.remote.GestionSolicitud
import com.duoc.aplicacionmunicipalidad.remote.MunicipalidadApi
import com.duoc.aplicacionmunicipalidad.remote.NuevoRequerimientoSolicitud
import com.duoc.aplicacionmunicipalidad.remote.RequerimientoRespuesta
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class RequerimientoRepositoryTest {

    private val api = mockk<MunicipalidadApi>()
    private val repositorio = RequerimientoRepository(api)

    private fun respuesta(id: String? = "SJ-2026-0001") = RequerimientoRespuesta(
        id = id, titulo = "Luminaria apagada", lugar = "Plaza", sector = "Carmen",
        categoria = "Alumbrado", prioridad = "Media", estado = "En atención", fechaMillis = 1000L,
        tipo = "Luminaria apagada", descripcion = "No enciende", evidenciaNombre = null,
        evidenciaDetalle = null, rutUsuario = "12.345.678-5", unidad = null, derivadoA = "Supervisor",
        observacion = null, observacionVisible = true, latitud = -33.49, longitud = -70.62
    )

    @Test
    fun `listar sin rut pide todos los requerimientos y los convierte al modelo`() = runTest {
        coEvery { api.listarRequerimientos(null) } returns listOf(respuesta())

        val lista = repositorio.listar()

        assertEquals(1, lista.size)
        assertEquals("SJ-2026-0001", lista[0].id)
        assertEquals("En atención", lista[0].estado)
        assertEquals(-33.49, lista[0].latitud ?: 0.0, 0.0001)
    }

    @Test
    fun `listar con rut filtra por ese vecino`() = runTest {
        coEvery { api.listarRequerimientos("12.345.678-5") } returns emptyList()

        val lista = repositorio.listar("12.345.678-5")

        assertEquals(0, lista.size)
        coVerify { api.listarRequerimientos("12.345.678-5") }
    }

    @Test
    fun `los campos nulos del servidor se reemplazan por valores por defecto`() = runTest {
        val sinDatos = RequerimientoRespuesta(
            null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null
        )
        coEvery { api.obtenerRequerimiento("X") } returns sinDatos

        val requerimiento = repositorio.obtener("X")

        assertEquals("", requerimiento.id)
        assertEquals("Recibido", requerimiento.estado)
        assertEquals(0L, requerimiento.fechaMillis)
        assertEquals("", requerimiento.observacion)
        assertFalse(requerimiento.observacionVisible)
        assertNull(requerimiento.latitud)
    }

    @Test
    fun `crear envia la solicitud y devuelve el requerimiento creado`() = runTest {
        val solicitud = NuevoRequerimientoSolicitud(
            categoria = "Alumbrado", tipo = "Luminaria apagada", lugar = "Plaza",
            descripcion = "No enciende hace días", prioridad = "Media", fechaMillis = 1000L,
            rutUsuario = "12.345.678-5", evidenciaNombre = "", evidenciaDetalle = ""
        )
        coEvery { api.crearRequerimiento(solicitud) } returns respuesta("SJ-2026-0149")

        val creado = repositorio.crear(solicitud)

        assertEquals("SJ-2026-0149", creado.id)
    }

    @Test
    fun `gestionar envia los cambios del funcionario`() = runTest {
        val solicitud = GestionSolicitud("Resuelto", "Aseo", "Supervisor", "Listo", true)
        coEvery { api.gestionarRequerimiento("SJ-2026-0001", solicitud) } returns respuesta()

        repositorio.gestionar("SJ-2026-0001", solicitud)

        coVerify { api.gestionarRequerimiento("SJ-2026-0001", solicitud) }
    }

    @Test
    fun `eliminar llama al endpoint delete con el codigo`() = runTest {
        coEvery { api.eliminarRequerimiento("SJ-2026-0001") } just Runs

        repositorio.eliminar("SJ-2026-0001")

        coVerify { api.eliminarRequerimiento("SJ-2026-0001") }
    }
}
