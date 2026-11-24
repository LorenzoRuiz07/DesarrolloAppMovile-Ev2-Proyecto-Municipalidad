package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.remote.ActualizarUsuarioSolicitud
import com.duoc.aplicacionmunicipalidad.remote.LoginSolicitud
import com.duoc.aplicacionmunicipalidad.remote.MunicipalidadApi
import com.duoc.aplicacionmunicipalidad.remote.RegistroSolicitud
import com.duoc.aplicacionmunicipalidad.remote.UsuarioRespuesta
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UsuarioRepositoryTest {

    private val api = mockk<MunicipalidadApi>()
    private val repositorio = UsuarioRepository(api)

    private val usuario = UsuarioRespuesta(
        rut = "12.345.678-5", nombre = "María Pérez Soto", correo = "maria@correo.cl",
        telefono = null, direccion = null, tipoPerfil = null
    )

    @Test
    fun `iniciar sesion quita los espacios del identificador y convierte el perfil`() = runTest {
        coEvery { api.iniciarSesion(LoginSolicitud("maria@correo.cl", "123456")) } returns usuario

        val perfil = repositorio.iniciarSesion("  maria@correo.cl  ", "123456")

        assertEquals("María Pérez Soto", perfil.nombre)
        assertEquals("12.345.678-5", perfil.rut)
    }

    @Test
    fun `si el servidor no entrega perfil se asume residente y los textos vacios`() = runTest {
        coEvery { api.obtenerUsuario("12.345.678-5") } returns usuario

        val perfil = repositorio.obtenerPorRut("12.345.678-5")

        assertEquals("Residente", perfil.tipoPerfil)
        assertEquals("", perfil.telefono)
        assertEquals("", perfil.direccion)
    }

    @Test
    fun `registrar envia los datos y devuelve el perfil creado`() = runTest {
        val solicitud = RegistroSolicitud(
            "12.345.678-5", "María Pérez Soto", "maria@correo.cl", "", "", "123456"
        )
        coEvery { api.registrar(solicitud) } returns usuario

        val perfil = repositorio.registrar(solicitud)

        assertEquals("maria@correo.cl", perfil.correo)
    }

    @Test
    fun `actualizar envia los datos editables del perfil`() = runTest {
        val solicitud = ActualizarUsuarioSolicitud("María P.", "nuevo@correo.cl", "+56 9 1111 2222", "Carmen 1")
        coEvery { api.actualizarUsuario("12.345.678-5", solicitud) } returns
                usuario.copy(nombre = "María P.", correo = "nuevo@correo.cl", tipoPerfil = "Residente")

        val perfil = repositorio.actualizar(
            "12.345.678-5", "María P.", "nuevo@correo.cl", "+56 9 1111 2222", "Carmen 1"
        )

        assertEquals("María P.", perfil.nombre)
        assertEquals("nuevo@correo.cl", perfil.correo)
    }
}
