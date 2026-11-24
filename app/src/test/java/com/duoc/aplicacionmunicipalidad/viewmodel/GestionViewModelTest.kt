package com.duoc.aplicacionmunicipalidad.viewmodel

import com.duoc.aplicacionmunicipalidad.ReglaDispatcherPrincipal
import com.duoc.aplicacionmunicipalidad.errorHttp
import com.duoc.aplicacionmunicipalidad.errorSinConexion
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.remote.GestionSolicitud
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GestionViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorio = mockk<RequerimientoRepository>()
    private val viewModel = GestionViewModel(repositorio)

    private val requerimiento = Requerimiento(
        id = "SJ-2026-0148", titulo = "Luminaria apagada", lugar = "Plaza", sector = "Carmen",
        categoria = "Alumbrado", prioridad = "Media", estado = "Recibido", fechaMillis = 1000L,
        observacion = "Pendiente de revisión", observacionVisible = true
    )

    @Before
    fun iniciarSesionComoSupervisor() {
        SesionUsuario.perfil = "Supervisor"
    }

    @After
    fun limpiarSesion() {
        SesionUsuario.perfil = "Residente"
    }

    private fun cargarRequerimiento() {
        coEvery { repositorio.obtener("SJ-2026-0148") } returns requerimiento
        viewModel.cargar("SJ-2026-0148")
    }

    @Test
    fun `cargar rellena el formulario con los valores del requerimiento`() {
        cargarRequerimiento()

        val estado = viewModel.uiState.value
        assertEquals("Recibido", estado.estado)
        assertEquals("Pendiente de revisión", estado.observacion)
        assertTrue(estado.visibleParaResidente)
        assertTrue(estado.etapas.isNotEmpty())
        assertFalse(estado.cargando)
    }

    @Test
    fun `si el caso no tiene derivacion se usa el perfil del funcionario`() {
        cargarRequerimiento()

        assertEquals("Supervisor", viewModel.uiState.value.derivadoA)
    }

    @Test
    fun `si no se encuentra el requerimiento queda como no encontrado`() {
        coEvery { repositorio.obtener("X") } throws errorHttp(404, """{"mensaje":"No existe."}""")

        viewModel.cargar("X")

        assertNull(viewModel.uiState.value.requerimiento)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun `los cambios del formulario limpian el aviso de guardado`() {
        viewModel.onEstadoChange("En atención")
        viewModel.onUnidadChange("Alumbrado Público")
        viewModel.onDerivadoChange("Operador")
        viewModel.onVisibleChange(true)

        val estado = viewModel.uiState.value
        assertEquals("En atención", estado.estado)
        assertEquals("Alumbrado Público", estado.unidad)
        assertEquals("Operador", estado.derivadoA)
        assertFalse(estado.guardado)
    }

    @Test
    fun `la observacion no admite mas de 500 caracteres`() {
        viewModel.onObservacionChange("a".repeat(500))
        viewModel.onObservacionChange("a".repeat(501))

        assertEquals(500, viewModel.uiState.value.observacion.length)
    }

    @Test
    fun `no se puede resolver un caso sin observacion`() {
        cargarRequerimiento()
        viewModel.onEstadoChange("Resuelto")
        viewModel.onObservacionChange("")

        viewModel.guardar()

        assertNotNull(viewModel.uiState.value.error)
        coVerify(exactly = 0) { repositorio.gestionar(any(), any()) }
    }

    @Test
    fun `guardar envia los cambios y actualiza el requerimiento`() {
        cargarRequerimiento()
        viewModel.onEstadoChange("Resuelto")
        viewModel.onObservacionChange("  Luminaria reparada  ")
        coEvery { repositorio.gestionar("SJ-2026-0148", any()) } returns
                requerimiento.copy(estado = "Resuelto", observacion = "Luminaria reparada")

        viewModel.guardar()

        val estado = viewModel.uiState.value
        assertTrue(estado.guardado)
        assertEquals("Resuelto", estado.requerimiento?.estado)
        coVerify {
            repositorio.gestionar(
                "SJ-2026-0148",
                match<GestionSolicitud> { it.estado == "Resuelto" && it.observacion == "Luminaria reparada" }
            )
        }
    }

    @Test
    fun `si falla el guardado muestra el error y no marca guardado`() {
        cargarRequerimiento()
        coEvery { repositorio.gestionar(any(), any()) } throws errorSinConexion()

        viewModel.guardar()

        assertFalse(viewModel.uiState.value.guardado)
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `guardar sin requerimiento cargado no hace nada`() {
        viewModel.guardar()

        coVerify(exactly = 0) { repositorio.gestionar(any(), any()) }
    }

    @Test
    fun `eliminar borra el requerimiento en el servidor`() {
        cargarRequerimiento()
        coEvery { repositorio.eliminar("SJ-2026-0148") } just Runs

        viewModel.eliminar()

        assertTrue(viewModel.uiState.value.eliminado)
        coVerify { repositorio.eliminar("SJ-2026-0148") }
    }

    @Test
    fun `si falla la eliminacion muestra el error`() {
        cargarRequerimiento()
        coEvery { repositorio.eliminar(any()) } throws errorSinConexion()

        viewModel.eliminar()

        assertFalse(viewModel.uiState.value.eliminado)
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `eliminar sin requerimiento cargado no hace nada`() {
        viewModel.eliminar()

        coVerify(exactly = 0) { repositorio.eliminar(any()) }
    }
}
