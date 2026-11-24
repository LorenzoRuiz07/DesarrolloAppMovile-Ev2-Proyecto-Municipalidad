package com.duoc.aplicacionmunicipalidad.viewmodel

import com.duoc.aplicacionmunicipalidad.ReglaDispatcherPrincipal
import com.duoc.aplicacionmunicipalidad.errorSinConexion
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorio = mockk<RequerimientoRepository>()
    private val viewModel = HomeViewModel(repositorio)

    private fun requerimiento(
        id: String,
        titulo: String = "Luminaria apagada",
        estado: String = "Recibido",
        categoria: String = "Alumbrado",
        prioridad: String = "Media",
        fecha: Long = 1000L
    ) = Requerimiento(
        id = id, titulo = titulo, lugar = "Plaza Valdivieso", sector = "Carmen",
        categoria = categoria, prioridad = prioridad, estado = estado, fechaMillis = fecha
    )

    private val lista = listOf(
        requerimiento("SJ-2026-0001", "Luminaria apagada", "Recibido", "Alumbrado", "Media", 1000L),
        requerimiento("SJ-2026-0002", "Riña en la calle", "En atención", "Seguridad", "Alta", 3000L),
        requerimiento("SJ-2026-0003", "Basura acumulada", "Resuelto", "Espacio público", "Baja", 2000L)
    )

    private fun cargarLista() {
        coEvery { repositorio.listar(null) } returns lista
        viewModel.cargar()
    }

    @Test
    fun `cargar descarga los requerimientos del servidor`() {
        cargarLista()

        val estado = viewModel.uiState.value
        assertEquals(3, estado.requerimientos.size)
        assertFalse(estado.cargando)
        assertEquals(null, estado.error)
    }

    @Test
    fun `si falla la conexion guarda el mensaje de error`() {
        coEvery { repositorio.listar(null) } throws errorSinConexion()

        viewModel.cargar()

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertNotNull(estado.error)
        assertTrue(estado.requerimientos.isEmpty())
    }

    @Test
    fun `los requerimientos visibles se ordenan del mas reciente al mas antiguo`() {
        cargarLista()

        val ids = viewModel.uiState.value.visibles.map { it.id }

        assertEquals(listOf("SJ-2026-0002", "SJ-2026-0003", "SJ-2026-0001"), ids)
    }

    @Test
    fun `la busqueda filtra por titulo sin distinguir mayusculas`() {
        cargarLista()

        viewModel.onConsultaChange("riña")

        assertEquals(listOf("SJ-2026-0002"), viewModel.uiState.value.visibles.map { it.id })
    }

    @Test
    fun `la busqueda tambien encuentra por codigo`() {
        cargarLista()

        viewModel.onConsultaChange("0003")

        assertEquals(listOf("SJ-2026-0003"), viewModel.uiState.value.visibles.map { it.id })
    }

    @Test
    fun `el filtro de estado deja solo los requerimientos de ese estado`() {
        cargarLista()

        viewModel.onEstadoChange("Resuelto")

        assertEquals(listOf("SJ-2026-0003"), viewModel.uiState.value.visibles.map { it.id })
    }

    @Test
    fun `los filtros de categoria y prioridad se pueden combinar`() {
        cargarLista()

        viewModel.onCategoriaChange("Seguridad")
        viewModel.onPrioridadChange("Alta")

        assertEquals(listOf("SJ-2026-0002"), viewModel.uiState.value.visibles.map { it.id })

        viewModel.onPrioridadChange("Baja")

        assertTrue(viewModel.uiState.value.visibles.isEmpty())
    }

    @Test
    fun `se puede alternar entre feed y mapa`() {
        viewModel.onVerMapaChange(true)

        assertTrue(viewModel.uiState.value.verMapa)
    }
}
