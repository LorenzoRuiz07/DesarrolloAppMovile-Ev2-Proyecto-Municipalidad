package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.remote.ActualizarUsuarioSolicitud
import com.duoc.aplicacionmunicipalidad.remote.LoginSolicitud
import com.duoc.aplicacionmunicipalidad.remote.MunicipalidadApi
import com.duoc.aplicacionmunicipalidad.remote.RegistroSolicitud
import com.duoc.aplicacionmunicipalidad.remote.RetrofitClient

/** Usuarios y autenticación a través del microservicio. Lanza excepciones si la red falla. */
class UsuarioRepository(
    private val api: MunicipalidadApi = RetrofitClient.api
) {

    suspend fun iniciarSesion(identificador: String, password: String): PerfilUsuario =
        api.iniciarSesion(LoginSolicitud(identificador.trim(), password)).aPerfilUsuario()

    suspend fun registrar(solicitud: RegistroSolicitud): PerfilUsuario =
        api.registrar(solicitud).aPerfilUsuario()

    suspend fun obtenerPorRut(rut: String): PerfilUsuario =
        api.obtenerUsuario(rut).aPerfilUsuario()

    suspend fun actualizar(
        rut: String,
        nombre: String,
        correo: String,
        telefono: String,
        direccion: String
    ): PerfilUsuario = api.actualizarUsuario(
        rut,
        ActualizarUsuarioSolicitud(nombre, correo, telefono, direccion)
    ).aPerfilUsuario()
}
