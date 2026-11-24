package com.duoc.aplicacionmunicipalidad

import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

/** Errores de red de ejemplo para las pruebas. */
fun errorHttp(codigo: Int, cuerpoJson: String): HttpException =
    HttpException(Response.error<Any>(codigo, cuerpoJson.toResponseBody("application/json".toMediaType())))

fun errorSinConexion(): IOException = IOException("sin conexión")
