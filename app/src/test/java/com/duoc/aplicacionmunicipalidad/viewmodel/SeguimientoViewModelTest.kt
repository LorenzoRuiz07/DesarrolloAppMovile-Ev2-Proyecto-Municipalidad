package com.duoc.aplicacionmunicipalidad.viewmodel

import com.duoc.aplicacionmunicipalidad.ReglaDispatcherPrincipal
import com.duoc.aplicacionmunicipalidad.errorHttp
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SeguimientoViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorio = mockk<RequerimientoRepository>()
    private val viewModel = SeguimientoViewModel(repositorio)

    private val requerimiento = Requerimiento(
        id = "SJ-2026-0148", titulo = "Luminaria apagada", lugar = "Plaza", sector = "Carmen",
        categoria = "Alumbrado", prioridad = "Media", estado = "En atención", fechaMillis = 1000L
    )

    @Test
    fun `cargar muestra el requerimiento y construye sus etapas`() {
        coEvery { repositorio.obtener("SJ-2026-0148") } returns requerimiento

        viewModel.cargar("SJ-2026-0148")

        val estado = viewModel.uiState.value
        assertEquals("SJ-2026-0148", estado.requerimiento?.id)
        assertTrue(estado.etapas.isNotEmpty())
        assertFalse(estado.cargando)
    }

    @Test
    fun `si el requerimiento no existe queda como no encontrado`() {
        coEvery { repositorio.obtener("NO-EXISTE") } throws
                errorHttp(404, """{"mensaje":"No existe el requerimiento."}""")

        viewModel.cargar("NO-EXISTE")

        val estado = viewModel.uiState.value
        assertNull(estado.requerimiento)
        assertTrue(estado.etapas.isEmpty())
        assertFalse(estado.cargando)
    }

    @Test
    fun `los avisos se activan y desactivan con cada toque`() {
        assertTrue(viewModel.uiState.value.avisosActivados)

        viewModel.onAvisosClick()
        assertFalse(viewModel.uiState.value.avisosActivados)

        viewModel.onAvisosClick()
        assertTrue(viewModel.uiState.value.avisosActivados)
    }
}
