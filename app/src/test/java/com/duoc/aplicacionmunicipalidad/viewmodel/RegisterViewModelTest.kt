package com.duoc.aplicacionmunicipalidad.viewmodel

import com.duoc.aplicacionmunicipalidad.ReglaDispatcherPrincipal
import com.duoc.aplicacionmunicipalidad.errorHttp
import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.remote.RegistroSolicitud
import com.duoc.aplicacionmunicipalidad.repository.UsuarioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorio = mockk<UsuarioRepository>()
    private val viewModel = RegisterViewModel(repositorio)

    private val perfilCreado = PerfilUsuario(
        "María Pérez Soto", "12.345.678-5", "maria@correo.cl", "", "", "Residente"
    )

    // Registra con datos válidos, cambiando solo el campo que cada prueba quiere probar
    private fun registrar(
        nombre: String = "María Pérez Soto",
        rut: String = "12.345.678-5",
        correo: String = "maria@correo.cl",
        telefono: String = "+56 9 1234 5678",
        password: String = "123456",
        confirmar: String = "123456",
        terminos: Boolean = true
    ) = viewModel.register(nombre, rut, correo, telefono, "Carmen 1234", password, confirmar, terminos)

    private fun mensajeDeError(): String = (viewModel.registerState.value as RegisterState.Error).message

    @Test
    fun `con campos obligatorios vacios muestra error`() {
        registrar(nombre = "")

        assertTrue(mensajeDeError().contains("Completa todos los campos"))
    }

    @Test
    fun `exige nombre y apellido`() {
        registrar(nombre = "María")

        assertTrue(mensajeDeError().contains("nombre completo"))
    }

    @Test
    fun `rechaza un rut con digito verificador incorrecto`() {
        registrar(rut = "12.345.678-9")

        assertTrue(mensajeDeError().contains("RUT"))
    }

    @Test
    fun `rechaza un rut con letras en el cuerpo`() {
        registrar(rut = "12.3A5.678-5")

        assertTrue(mensajeDeError().contains("RUT"))
    }

    @Test
    fun `acepta un rut con digito verificador K`() {
        coEvery { repositorio.registrar(any()) } returns perfilCreado

        registrar(rut = "10.000.013-K")

        assertEquals(RegisterState.Success, viewModel.registerState.value)
    }

    @Test
    fun `rechaza un correo sin formato valido`() {
        registrar(correo = "maria@correo")

        assertTrue(mensajeDeError().contains("correo"))
    }

    @Test
    fun `rechaza un telefono demasiado corto`() {
        registrar(telefono = "1234")

        assertTrue(mensajeDeError().contains("teléfono"))
    }

    @Test
    fun `rechaza una contrasena de menos de 6 caracteres`() {
        registrar(password = "123", confirmar = "123")

        assertTrue(mensajeDeError().contains("al menos 6"))
    }

    @Test
    fun `rechaza contrasenas que no coinciden`() {
        registrar(confirmar = "654321")

        assertTrue(mensajeDeError().contains("no coinciden"))
    }

    @Test
    fun `exige aceptar los terminos`() {
        registrar(terminos = false)

        assertTrue(mensajeDeError().contains("términos"))
    }

    @Test
    fun `con datos validos registra al usuario y termina en exito`() {
        coEvery { repositorio.registrar(any()) } returns perfilCreado

        registrar(nombre = "  María Pérez Soto  ")

        assertEquals(RegisterState.Success, viewModel.registerState.value)
        coVerify {
            repositorio.registrar(
                RegistroSolicitud(
                    "12.345.678-5", "María Pérez Soto", "maria@correo.cl",
                    "+56 9 1234 5678", "Carmen 1234", "123456"
                )
            )
        }
    }

    @Test
    fun `si el rut ya existe muestra el mensaje del servidor`() {
        coEvery { repositorio.registrar(any()) } throws
                errorHttp(409, """{"mensaje":"Ya existe un usuario registrado con este RUT."}""")

        registrar()

        assertEquals("Ya existe un usuario registrado con este RUT.", mensajeDeError())
    }

    @Test
    fun `resetState vuelve al estado de reposo`() {
        registrar(nombre = "")

        viewModel.resetState()

        assertEquals(RegisterState.Idle, viewModel.registerState.value)
    }
}
