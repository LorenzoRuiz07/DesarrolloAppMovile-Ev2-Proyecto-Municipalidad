package com.duoc.aplicacionmunicipalidad.viewmodel

import com.duoc.aplicacionmunicipalidad.ReglaDispatcherPrincipal
import com.duoc.aplicacionmunicipalidad.errorHttp
import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.remote.NuevoRequerimientoSolicitud
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NuevoRequerimientoViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorio = mockk<RequerimientoRepository>()
    private val viewModel = NuevoRequerimientoViewModel(repositorio)

    private val rutResidente = "12.345.678-5"

    @Before
    fun iniciarSesion() {
        SesionUsuario.perfilUsuarioActual = PerfilUsuario(
            "María Pérez Soto", rutResidente, "maria@correo.cl", "", "", "Residente"
        )
    }

    @After
    fun limpiarSesion() {
        SesionUsuario.perfilUsuarioActual = null
    }

    // Deja el formulario completo y válido
    private fun completarFormulario() {
        viewModel.onCategoriaChange("Alumbrado")
        viewModel.onTipoChange("Luminaria apagada")
        viewModel.onLugarChange("Plaza Valdivieso")
        viewModel.onDescripcionChange("La luminaria no enciende desde hace una semana")
    }

    private fun requerimientoCreado(id: String) = Requerimiento(
        id = id, titulo = "Luminaria apagada en Plaza Valdivieso", lugar = "Plaza Valdivieso",
        sector = "San Joaquín", categoria = "Alumbrado", prioridad = "Media",
        estado = "Recibido", fechaMillis = 1000L
    )

    @Test
    fun `al cambiar la categoria se borra el tipo elegido`() {
        viewModel.onCategoriaChange("Alumbrado")
        viewModel.onTipoChange("Luminaria apagada")

        viewModel.onCategoriaChange("Tránsito")

        assertEquals("Tránsito", viewModel.uiState.value.categoria)
        assertEquals("", viewModel.uiState.value.tipo)
    }

    @Test
    fun `la descripcion no admite mas de 500 caracteres`() {
        viewModel.onDescripcionChange("a".repeat(500))
        viewModel.onDescripcionChange("a".repeat(501))

        assertEquals(500, viewModel.uiState.value.descripcion.length)
    }

    @Test
    fun `sin categoria ni tipo no se envia`() {
        viewModel.enviar()

        assertTrue(viewModel.uiState.value.error!!.contains("categoría"))
        coVerify(exactly = 0) { repositorio.crear(any()) }
    }

    @Test
    fun `un lugar muy corto no se envia`() {
        completarFormulario()
        viewModel.onLugarChange("ab")

        viewModel.enviar()

        assertTrue(viewModel.uiState.value.error!!.contains("dónde ocurre"))
    }

    @Test
    fun `una descripcion muy corta no se envia`() {
        completarFormulario()
        viewModel.onDescripcionChange("corta")

        viewModel.enviar()

        assertTrue(viewModel.uiState.value.error!!.contains("mínimo 10"))
    }

    @Test
    fun `una fecha futura no se envia`() {
        completarFormulario()
        viewModel.onFechaSeleccionada(System.currentTimeMillis() + 400L * 24 * 60 * 60 * 1000)

        viewModel.enviar()

        assertTrue(viewModel.uiState.value.error!!.contains("futuras"))
    }

    @Test
    fun `con el formulario completo envia el requerimiento con el rut del usuario`() {
        val solicitud = slot<NuevoRequerimientoSolicitud>()
        coEvery { repositorio.crear(capture(solicitud)) } returns requerimientoCreado("SJ-2026-0149")
        completarFormulario()
        viewModel.onPrioridadChange("Alta")

        viewModel.enviar()

        val estado = viewModel.uiState.value
        assertEquals("SJ-2026-0149", estado.idEnviado)
        assertFalse(estado.enviando)
        assertNull(estado.error)
        assertEquals(rutResidente, solicitud.captured.rutUsuario)
        assertEquals("Alta", solicitud.captured.prioridad)
        assertEquals("Plaza Valdivieso", solicitud.captured.lugar)
    }

    @Test
    fun `si el servidor rechaza el requerimiento muestra su mensaje`() {
        coEvery { repositorio.crear(any()) } throws errorHttp(400, """{"mensaje":"Datos inválidos."}""")
        completarFormulario()

        viewModel.enviar()

        assertEquals("Datos inválidos.", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.idEnviado)
    }

    @Test
    fun `se puede adjuntar y quitar una evidencia`() {
        viewModel.onAdjuntarEvidencia()
        assertNotNull(viewModel.uiState.value.evidencia)

        viewModel.onQuitarEvidencia()
        assertNull(viewModel.uiState.value.evidencia)
    }

    @Test
    fun `la ubicacion del gps guarda las coordenadas y rellena el lugar`() {
        viewModel.onUbicacionObtenida(-33.4975, -70.6240)

        val estado = viewModel.uiState.value
        assertEquals(-33.4975, estado.latitud ?: 0.0, 0.00001)
        assertEquals(-70.6240, estado.longitud ?: 0.0, 0.00001)
        assertEquals("Ubicación GPS: -33.49750, -70.62400", estado.lugar)
    }

    @Test
    fun `la hora seleccionada se refleja en el estado`() {
        viewModel.onHoraSeleccionada(18, 40)

        assertEquals(18, viewModel.uiState.value.hora)
        assertEquals(40, viewModel.uiState.value.minuto)
        assertEquals("18:40", viewModel.uiState.value.horaTexto)
    }
}
