package com.duoc.aplicacionmunicipalidad.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ---------------------------------------------------------------------------------------------
// API externa: Overpass (OpenStreetMap). Gratuita y sin clave. Entrega lugares reales del mapa.
// Documentación: https://wiki.openstreetmap.org/wiki/Overpass_API
// ---------------------------------------------------------------------------------------------

data class OverpassRespuesta(val elements: List<ElementoOsm>?)

data class ElementoOsm(
    val id: Long?,
    val lat: Double?,          // los puntos (node) traen lat y lon
    val lon: Double?,
    val center: CentroOsm?,    // los edificios (way) traen el centro en "center"
    val tags: Map<String, String>?
)

data class CentroOsm(val lat: Double?, val lon: Double?)

interface OverpassApi {

    // La consulta se escribe en el lenguaje de Overpass y viene en el parámetro "data"
    @GET("api/interpreter")
    suspend fun buscar(@Query("data") consulta: String): OverpassRespuesta
}

object OverpassClient {

    private const val URL_BASE = "https://overpass-api.de/"

    // Overpass pide identificar la aplicación que hace las consultas
    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor { cadena ->
            cadena.proceed(
                cadena.request().newBuilder()
                    .header("User-Agent", "AppMunicipalidadSanJoaquin/1.0 (proyecto Duoc UC)")
                    .build()
            )
        }
        .build()

    val api: OverpassApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OverpassApi::class.java)
    }
}
