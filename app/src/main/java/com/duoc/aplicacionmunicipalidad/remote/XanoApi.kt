package com.duoc.aplicacionmunicipalidad.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


data class PuntoSeguridadRespuesta(
    val id: Long?,
    val nombre: String?,
    val tipo: String?,
    val direccion: String?,
    val latitud: Double?,
    val longitud: Double?
)

interface XanoApi {

    @GET("puntos_seguridad")
    suspend fun obtenerPuntosSeguridad(): List<PuntoSeguridadRespuesta>
}

object XanoClient {

    private const val URL_BASE = "https://x8ki-letl-twmt.n7.xano.io/api:mvyYdc1k/"

    val api: XanoApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XanoApi::class.java)
    }
}
