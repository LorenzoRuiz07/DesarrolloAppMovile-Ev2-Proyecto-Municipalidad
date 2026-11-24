package com.duoc.aplicacionmunicipalidad.viewmodel

import com.duoc.aplicacionmunicipalidad.ReglaDispatcherPrincipal
import com.duoc.aplicacionmunicipalidad.errorSinConexion
import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MisCasosViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorio = mockk<RequerimientoRepository>()
    private val viewModel = MisCasosViewModel(repositorio)

    private val rutResidente = "12.345.678-5"

    private fun requerimiento(id: String, estado: String, fecha: Long) = Requerimiento(
        id = id, titulo = "Caso $id", lugar = "Plaza", sector = "Carmen",
        categoria = "Alumbrado", prioridad = "Media", estado = estado, fechaMillis = fecha
    )

    @Before
    fun iniciarSesionComoResidente() {
        SesionUsuario.perfil = "Residente"
        SesionUsuario.perfilUsuarioActual = PerfilUsuario(
            "María Pérez Soto", rutResidente, "maria@correo.cl", "", "", "Residente"
        )
    }

    @After
    fun limpiarSesion() {
        SesionUsuario.perfil = "Residente"
        SesionUsuario.perfilUsuarioActual = null
    }

    @Test
    fun `el residente descarga solo sus propios casos`() {
        coEvery { repositorio.listar(rutResidente) } returns listOf(requerimiento("A", "Recibido", 1L))

        viewModel.cargar()

        coVerify { repositorio.listar(rutResidente) }
        assertFalse(viewModel.uiState.value.esFuncionario)
        assertEquals(1, viewModel.uiState.value.requerimientos.size)
    }

    @Test
    fun `el funcionario descarga todos los casos`() {
        SesionUsuario.perfil = "Operador"
        coEvery { repositorio.listar(null) } returns emptyList()

        viewModel.cargar()

        coVerify { repositorio.listar(null) }
        assertTrue(viewModel.uiState.value.esFuncionario)
    }

    @Test
    fun `si falla la descarga guarda el error`() {
        coEvery { repositorio.listar(rutResidente) } throws errorSinConexion()

        viewModel.cargar()

        assertNotNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun `los filtros separan casos en curso y resueltos`() {
        coEvery { repositorio.listar(rutResidente) } returns listOf(
            requerimiento("A", "Recibido", 1L),
            requerimiento("B", "En atención", 3L),
            requerimiento("C", "Resuelto", 2L)
        )
        viewModel.cargar()

        assertEquals(listOf("B", "C", "A"), viewModel.uiState.value.visibles.map { it.id })

        viewModel.onFiltroChange("En curso")
        assertEquals(listOf("B", "A"), viewModel.uiState.value.visibles.map { it.id })

        viewModel.onFiltroChange("Resueltos")
        assertEquals(listOf("C"), viewModel.uiState.value.visibles.map { it.id })
    }
}
