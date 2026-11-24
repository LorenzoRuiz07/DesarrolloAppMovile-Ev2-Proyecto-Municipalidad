package com.duoc.aplicacionmunicipalidad.viewmodel

import com.duoc.aplicacionmunicipalidad.ReglaDispatcherPrincipal
import com.duoc.aplicacionmunicipalidad.errorSinConexion
import com.duoc.aplicacionmunicipalidad.model.PuntoSeguridad
import com.duoc.aplicacionmunicipalidad.repository.PuntosSeguridadRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PuntosSeguridadViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorio = mockk<PuntosSeguridadRepository>()
    private val viewModel = PuntosSeguridadViewModel(repositorio)

    private val puntos = listOf(
        PuntoSeguridad(1, "Comisaría", "Carabineros", "Calle 1", -33.48, -70.63),
        PuntoSeguridad(2, "Bomberos 5", "Bomberos", "Calle 2", -33.49, -70.62),
        PuntoSeguridad(3, "CESFAM", "Salud", "Calle 3", -33.50, -70.61)
    )

    @Test
    fun `al comenzar esta cargando`() {
        assertTrue(viewModel.uiState.value.cargando)
    }

    @Test
    fun `cargar entrega los puntos de la API`() {
        coEvery { repositorio.obtenerPuntos() } returns puntos

        viewModel.cargar()

        assertEquals(3, viewModel.uiState.value.puntos.size)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun `si la API falla guarda el error y permite reintentar`() {
        coEvery { repositorio.obtenerPuntos() } throws errorSinConexion()

        viewModel.cargar()

        assertNotNull(viewModel.uiState.value.error)

        coEvery { repositorio.obtenerPuntos() } returns puntos
        viewModel.cargar()

        assertEquals(null, viewModel.uiState.value.error)
        assertEquals(3, viewModel.uiState.value.puntos.size)
    }

    @Test
    fun `el filtro por tipo deja solo los puntos de ese tipo`() {
        coEvery { repositorio.obtenerPuntos() } returns puntos
        viewModel.cargar()

        viewModel.onFiltroTipoChange("Bomberos")
        assertEquals(listOf("Bomberos 5"), viewModel.uiState.value.visibles.map { it.nombre })

        viewModel.onFiltroTipoChange(null)
        assertEquals(3, viewModel.uiState.value.visibles.size)
    }
}
