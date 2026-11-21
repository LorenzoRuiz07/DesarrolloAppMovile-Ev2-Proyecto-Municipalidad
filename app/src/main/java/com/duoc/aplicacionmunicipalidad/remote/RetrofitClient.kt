package com.duoc.aplicacionmunicipalidad.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 es la dirección con la que el emulador de Android Studio llega al localhost del PC
    private const val URL_BASE = "http://10.0.2.2:8080/"

    val api: MunicipalidadApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MunicipalidadApi::class.java)
    }
}
