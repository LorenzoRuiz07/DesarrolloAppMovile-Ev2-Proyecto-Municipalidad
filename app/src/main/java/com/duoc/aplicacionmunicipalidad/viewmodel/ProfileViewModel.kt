package com.duoc.aplicacionmunicipalidad.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.AppDatabase
import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.model.Usuario
import com.duoc.aplicacionmunicipalidad.repository.SesionRepository
import com.duoc.aplicacionmunicipalidad.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class ProfileUiState(
    val perfil: PerfilUsuario,
    val notificaciones: Boolean = true,
    val mostrarDialogoSalir: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.obtener(application)
    private val usuarioRepository = UsuarioRepository(database.usuarioDao())
    private val sesionRepository = SesionRepository(database.sesionDao())

    private val defaultPerfil = PerfilUsuario(
        nombre = "María Pérez Soto",
        rut = "12.345.678-5",
        correo = "maria.perez@correo.cl",
        telefono = "+56 9 1234 5678",
        direccion = "Carmen 1234, San Joaquín",
        tipoPerfil = SesionUsuario.perfil
    )

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            perfil = SesionUsuario.perfilUsuarioActual ?: defaultPerfil
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onNotificacionesChange(activadas: Boolean) =
        _uiState.update { it.copy(notificaciones = activadas) }

    fun onSalirClick() = _uiState.update { it.copy(mostrarDialogoSalir = true) }

    fun onDialogoDismiss() = _uiState.update { it.copy(mostrarDialogoSalir = false) }

    fun onGuardarPerfil(
        nombre: String,
        rut: String,
        correo: String,
        telefono: String,
        direccion: String
    ) {
        val rutOriginal = _uiState.value.perfil.rut
        val fotoPerfilActual = _uiState.value.perfil.fotoPerfil
        val fotoPerfilPath = if (fotoPerfilActual is String) fotoPerfilActual else null

        val nuevoPerfil = _uiState.value.perfil.copy(
            nombre = nombre,
            rut = rut,
            correo = correo,
            telefono = telefono,
            direccion = direccion
        )
        SesionUsuario.perfilUsuarioActual = nuevoPerfil
        _uiState.update { it.copy(perfil = nuevoPerfil) }

        viewModelScope.launch {
            val usuarioActual = usuarioRepository.obtenerPorRut(rutOriginal)
            val password = usuarioActual?.password ?: "1234"
            val nuevoUsuario = Usuario(
                rut = rut,
                nombre = nombre,
                correo = correo,
                telefono = telefono,
                direccion = direccion,
                password = password,
                tipoPerfil = nuevoPerfil.tipoPerfil,
                fotoPerfilPath = fotoPerfilPath
            )
            usuarioRepository.actualizarPerfilUsuario(rutOriginal, nuevoUsuario)
            sesionRepository.guardar(perfil = nuevoPerfil.tipoPerfil, usuarioRut = rut)
        }
    }

    fun onActualizarFoto(foto: Any?) {
        val pathFoto = guardarFotoLocal(foto)
        val nuevoPerfil = _uiState.value.perfil.copy(fotoPerfil = pathFoto)
        SesionUsuario.perfilUsuarioActual = nuevoPerfil
        _uiState.update { it.copy(perfil = nuevoPerfil) }

        val rutActual = nuevoPerfil.rut
        viewModelScope.launch {
            val usuarioActual = usuarioRepository.obtenerPorRut(rutActual)
            if (usuarioActual != null) {
                val actualizado = usuarioActual.copy(fotoPerfilPath = pathFoto)
                usuarioRepository.guardarUsuario(actualizado)
            }
        }
    }

    private fun guardarFotoLocal(foto: Any?): String? {
        if (foto == null) return null
        if (foto is String) return foto
        return try {
            val app = getApplication<Application>()
            val file = File(app.filesDir, "foto_perfil_${System.currentTimeMillis()}.png")
            if (foto is Bitmap) {
                FileOutputStream(file).use { out ->
                    foto.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                file.absolutePath
            } else if (foto is Uri) {
                app.contentResolver.openInputStream(foto)?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                file.absolutePath
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
