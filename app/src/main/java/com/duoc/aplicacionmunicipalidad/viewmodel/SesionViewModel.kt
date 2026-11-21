package com.duoc.aplicacionmunicipalidad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.AppDatabase
import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.repository.SesionRepository
import com.duoc.aplicacionmunicipalidad.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SesionUiState(
    val cargando: Boolean = true,
    val perfil: String? = null
)

class SesionViewModel @JvmOverloads constructor(
    application: Application,
    private val sesionRepository: SesionRepository =
        SesionRepository(AppDatabase.obtener(application).sesionDao()),
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SesionUiState())
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    init {
        // Al abrir la app, busca si quedó una sesión guardada en el teléfono
        viewModelScope.launch {
            val sesion = sesionRepository.obtenerSesion()
            if (sesion != null) {
                SesionUsuario.perfil = sesion.perfil
                SesionUsuario.perfilUsuarioActual = recuperarPerfil(
                    rut = sesion.usuarioRut,
                    perfil = sesion.perfil,
                    fotoPerfilPath = sesion.fotoPerfilPath
                )
            }
            _uiState.value = SesionUiState(cargando = false, perfil = sesion?.perfil)
        }
    }

    // Pide los datos actualizados al servidor; si no hay conexión usa solo lo guardado en el teléfono
    private suspend fun recuperarPerfil(rut: String?, perfil: String, fotoPerfilPath: String?): PerfilUsuario? {
        if (rut == null) return null
        return try {
            usuarioRepository.obtenerPorRut(rut).copy(fotoPerfil = fotoPerfilPath)
        } catch (error: Exception) {
            PerfilUsuario(
                nombre = "Usuario",
                rut = rut,
                correo = "",
                telefono = "",
                direccion = "",
                tipoPerfil = perfil,
                fotoPerfil = fotoPerfilPath
            )
        }
    }

    // Se llama cuando el login es exitoso
    fun iniciarSesion(perfil: String, usuario: PerfilUsuario) {
        SesionUsuario.perfil = perfil
        SesionUsuario.perfilUsuarioActual = usuario
        viewModelScope.launch {
            sesionRepository.guardar(perfil = perfil, usuarioRut = usuario.rut)
        }
    }

    // Se llama al cerrar sesión
    fun cerrarSesion() {
        SesionUsuario.perfil = "Residente"
        SesionUsuario.perfilUsuarioActual = null
        viewModelScope.launch { sesionRepository.borrar() }
    }
}
