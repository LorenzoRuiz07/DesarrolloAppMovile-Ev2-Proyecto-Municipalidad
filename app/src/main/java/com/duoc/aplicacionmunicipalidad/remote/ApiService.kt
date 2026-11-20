package com.duoc.aplicacionmunicipalidad.remote

import retrofit2.http.GET

interface ApiService {
    @GET(" TU PATH ")
    suspend fun obtenerFrases(): List<Frase>
}
