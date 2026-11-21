package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.remote.ErrorRespuesta
import com.google.gson.Gson
import java.io.IOException
import retrofit2.HttpException

/**
 * Convierte cualquier error de red en un texto que se pueda mostrar al usuario.
 * El microservicio responde los errores como {"mensaje": "..."}.
 */
fun mensajeDeError(error: Throwable): String = when (error) {
    is HttpException -> {
        val textoJson = error.response()?.errorBody()?.string()
        val mensajeServidor = try {
            Gson().fromJson(textoJson, ErrorRespuesta::class.java)?.mensaje
        } catch (e: Exception) {
            null
        }
        mensajeServidor ?: "El servidor respondió con un error (${error.code()})."
    }
    is IOException -> "No se pudo conectar con el servidor. Revisa que el backend esté encendido."
    else -> "Ocurrió un error inesperado."
}
