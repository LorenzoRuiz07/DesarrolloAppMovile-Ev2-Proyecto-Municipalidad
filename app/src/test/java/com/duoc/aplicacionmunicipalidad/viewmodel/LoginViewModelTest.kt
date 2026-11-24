package com.duoc.aplicacionmunicipalidad.viewmodel

import com.duoc.aplicacionmunicipalidad.ReglaDispatcherPrincipal
import com.duoc.aplicacionmunicipalidad.errorHttp
import com.duoc.aplicacionmunicipalidad.errorSinConexion
import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.repository.UsuarioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorio = mockk<UsuarioRepository>()
    private val viewModel = LoginViewModel(repositorio)

    private val operador = PerfilUsuario(
        nombre = "Operador Municipal", rut = "11.111.111-1", correo = "operador@sanjoaquin.cl",
        telefono = "", direccion = "", tipoPerfil = "Operador"
    )

    @Test
    fun `el estado inicial es reposo`() {
        assertEquals(LoginState.Idle, viewModel.loginState.value)
    }

    @Test
    fun `con campos vacios muestra error y no consulta al servidor`() {
        viewModel.performLogin("", "")

        val estado = viewModel.loginState.value
        assertTrue(estado is LoginState.Error)
        coVerify(exactly = 0) { repositorio.iniciarSesion(any(), any()) }
    }

    @Test
    fun `con credenciales correctas entrega el perfil del usuario`() {
        coEvery { repositorio.iniciarSesion("operador@sanjoaquin.cl", "123456") } returns operador

        viewModel.performLogin("operador@sanjoaquin.cl", "123456")

        val estado = viewModel.loginState.value as LoginState.Success
        assertEquals("Operador", estado.userProfile)
        assertEquals("11.111.111-1", estado.usuario.rut)
    }

    @Test
    fun `con credenciales incorrectas muestra el mensaje del servidor`() {
        coEvery { repositorio.iniciarSesion(any(), any()) } throws
                errorHttp(401, """{"mensaje":"Credenciales incorrectas."}""")

        viewModel.performLogin("operador@sanjoaquin.cl", "mala")

        val estado = viewModel.loginState.value as LoginState.Error
        assertEquals("Credenciales incorrectas.", estado.message)
    }

    @Test
    fun `sin conexion informa que no se pudo conectar`() {
        coEvery { repositorio.iniciarSesion(any(), any()) } throws errorSinConexion()

        viewModel.performLogin("operador@sanjoaquin.cl", "123456")

        val estado = viewModel.loginState.value as LoginState.Error
        assertTrue(estado.message.contains("No se pudo conectar"))
    }

    @Test
    fun `resetState vuelve al estado de reposo`() {
        viewModel.performLogin("", "")

        viewModel.resetState()

        assertEquals(LoginState.Idle, viewModel.loginState.value)
    }
}
