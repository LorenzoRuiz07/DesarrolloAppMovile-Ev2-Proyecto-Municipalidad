package com.duoc.aplicacionmunicipalidad.remote

import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// ---------------------------------------------------------------------------------------------
// Solicitudes que la app envía al microservicio
// ---------------------------------------------------------------------------------------------

data class LoginSolicitud(val identificador: String, val password: String)

data class RegistroSolicitud(
    val rut: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val password: String
)

data class ActualizarUsuarioSolicitud(
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String
)

data class NuevoRequerimientoSolicitud(
    val categoria: String,
    val tipo: String,
    val lugar: String,
    val descripcion: String,
    val prioridad: String,
    val fechaMillis: Long,
    val rutUsuario: String,
    val evidenciaNombre: String,
    val evidenciaDetalle: String,
    val latitud: Double? = null,
    val longitud: Double? = null
)

data class GestionSolicitud(
    val estado: String,
    val unidad: String,
    val derivadoA: String,
    val observacion: String,
    val observacionVisible: Boolean
)

// ---------------------------------------------------------------------------------------------
// Respuestas del microservicio (los campos pueden venir nulos desde el JSON)
// ---------------------------------------------------------------------------------------------

data class UsuarioRespuesta(
    val rut: String?,
    val nombre: String?,
    val correo: String?,
    val telefono: String?,
    val direccion: String?,
    val tipoPerfil: String?
) {
    fun aPerfilUsuario(): PerfilUsuario = PerfilUsuario(
        nombre = nombre.orEmpty(),
        rut = rut.orEmpty(),
        correo = correo.orEmpty(),
        telefono = telefono.orEmpty(),
        direccion = direccion.orEmpty(),
        tipoPerfil = tipoPerfil ?: "Residente"
    )
}

data class RequerimientoRespuesta(
    val id: String?,
    val titulo: String?,
    val lugar: String?,
    val sector: String?,
    val categoria: String?,
    val prioridad: String?,
    val estado: String?,
    val fechaMillis: Long?,
    val tipo: String?,
    val descripcion: String?,
    val evidenciaNombre: String?,
    val evidenciaDetalle: String?,
    val rutUsuario: String?,
    val unidad: String?,
    val derivadoA: String?,
    val observacion: String?,
    val observacionVisible: Boolean?,
    val latitud: Double?,
    val longitud: Double?
) {
    fun aRequerimiento(): Requerimiento = Requerimiento(
        id = id.orEmpty(),
        titulo = titulo.orEmpty(),
        lugar = lugar.orEmpty(),
        sector = sector.orEmpty(),
        categoria = categoria.orEmpty(),
        prioridad = prioridad.orEmpty(),
        estado = estado ?: "Recibido",
        fechaMillis = fechaMillis ?: 0L,
        tipo = tipo.orEmpty(),
        descripcion = descripcion.orEmpty(),
        evidenciaNombre = evidenciaNombre.orEmpty(),
        evidenciaDetalle = evidenciaDetalle.orEmpty(),
        rutUsuario = rutUsuario.orEmpty(),
        unidad = unidad.orEmpty(),
        derivadoA = derivadoA.orEmpty(),
        observacion = observacion.orEmpty(),
        observacionVisible = observacionVisible ?: false,
        latitud = latitud,
        longitud = longitud
    )
}

data class ErrorRespuesta(val mensaje: String?)

// ---------------------------------------------------------------------------------------------
// Endpoints del microservicio municipalidad-api
// ---------------------------------------------------------------------------------------------

interface MunicipalidadApi {

    @POST("api/auth/login")
    suspend fun iniciarSesion(@Body solicitud: LoginSolicitud): UsuarioRespuesta

    @POST("api/auth/registro")
    suspend fun registrar(@Body solicitud: RegistroSolicitud): UsuarioRespuesta

    @GET("api/usuarios/{rut}")
    suspend fun obtenerUsuario(@Path("rut") rut: String): UsuarioRespuesta

    @PUT("api/usuarios/{rut}")
    suspend fun actualizarUsuario(
        @Path("rut") rut: String,
        @Body solicitud: ActualizarUsuarioSolicitud
    ): UsuarioRespuesta

    @GET("api/requerimientos")
    suspend fun listarRequerimientos(
        @Query("rutUsuario") rutUsuario: String? = null
    ): List<RequerimientoRespuesta>

    @GET("api/requerimientos/{id}")
    suspend fun obtenerRequerimiento(@Path("id") id: String): RequerimientoRespuesta

    @POST("api/requerimientos")
    suspend fun crearRequerimiento(@Body solicitud: NuevoRequerimientoSolicitud): RequerimientoRespuesta

    @PUT("api/requerimientos/{id}")
    suspend fun gestionarRequerimiento(
        @Path("id") id: String,
        @Body solicitud: GestionSolicitud
    ): RequerimientoRespuesta

    @DELETE("api/requerimientos/{id}")
    suspend fun eliminarRequerimiento(@Path("id") id: String)
}
