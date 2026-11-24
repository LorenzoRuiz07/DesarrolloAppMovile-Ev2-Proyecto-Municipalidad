package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.errorHttp
import com.duoc.aplicacionmunicipalidad.errorSinConexion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ErroresApiTest {

    @Test
    fun `un error http con mensaje del servidor devuelve ese mensaje`() {
        val error = errorHttp(401, """{"mensaje":"Credenciales incorrectas."}""")

        assertEquals("Credenciales incorrectas.", mensajeDeError(error))
    }

    @Test
    fun `un error http con cuerpo ilegible devuelve un mensaje con el codigo`() {
        val error = errorHttp(500, "esto no es json")

        assertTrue(mensajeDeError(error).contains("500"))
    }

    @Test
    fun `un error http sin mensaje en el json devuelve un mensaje con el codigo`() {
        val error = errorHttp(404, "{}")

        assertTrue(mensajeDeError(error).contains("404"))
    }

    @Test
    fun `un error de conexion pide revisar el backend`() {
        assertTrue(mensajeDeError(errorSinConexion()).contains("No se pudo conectar"))
    }

    @Test
    fun `cualquier otro error devuelve un mensaje generico`() {
        assertEquals("Ocurrió un error inesperado.", mensajeDeError(IllegalStateException("x")))
    }
}
