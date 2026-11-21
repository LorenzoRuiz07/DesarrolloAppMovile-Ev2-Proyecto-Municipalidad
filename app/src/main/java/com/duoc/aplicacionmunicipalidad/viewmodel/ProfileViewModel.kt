package com.duoc.aplicacionmunicipalidad.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.AppDatabase
import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.repository.SesionRepository
import com.duoc.aplicacionmunicipalidad.repository.UsuarioRepository
import com.duoc.aplicacionmunicipalidad.repository.mensajeDeError
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
    val mostrarDialogoSalir: Boolean = false,
    val mensaje: String? = null
)

class ProfileViewModel @JvmOverloads constructor(
    application: Application,
    private val usuarioRepository: UsuarioRepository = UsuarioRepository(),
    private val sesionRepository: SesionRepository =
        SesionRepository(AppDatabase.obtener(application).sesionDao())
) : AndroidViewModel(application) {

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

    fun onMensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    // El RUT identifica al usuario en el servidor, por eso no se puede cambiar
    fun onGuardarPerfil(
        nombre: String,
        correo: String,
        telefono: String,
        direccion: String
    ) {
        val perfilActual = _uiState.value.perfil

        viewModelScope.launch {
            try {
                val actualizado = usuarioRepository.actualizar(
                    rut = perfilActual.rut,
                    nombre = nombre.trim(),
                    correo = correo.trim(),
                    telefono = telefono.trim(),
                    direccion = direccion.trim()
                ).copy(fotoPerfil = perfilActual.fotoPerfil)

                SesionUsuario.perfilUsuarioActual = actualizado
                _uiState.update { it.copy(perfil = actualizado, mensaje = "Perfil actualizado.") }
            } catch (error: Exception) {
                _uiState.update { it.copy(mensaje = mensajeDeError(error)) }
            }
        }
    }

    fun onActualizarFoto(foto: Any?) {
        val rutaFoto = guardarFotoLocal(foto)
        val nuevoPerfil = _uiState.value.perfil.copy(fotoPerfil = rutaFoto)
        SesionUsuario.perfilUsuarioActual = nuevoPerfil
        _uiState.update { it.copy(perfil = nuevoPerfil) }

        viewModelScope.launch { sesionRepository.guardarFoto(rutaFoto) }
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
