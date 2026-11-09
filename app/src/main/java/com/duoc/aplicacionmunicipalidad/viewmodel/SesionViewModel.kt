package com.duoc.aplicacionmunicipalidad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.AppDatabase
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.model.Usuario
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

class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.obtener(application)
    private val sesionRepository = SesionRepository(database.sesionDao())
    private val usuarioRepository = UsuarioRepository(database.usuarioDao())

    private val _uiState = MutableStateFlow(SesionUiState())
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    init {
        //Al abrir la app, busca si quedó una sesión guardada
        viewModelScope.launch {
            usuarioRepository.inicializarUsuariosPorDefecto()
            val sesion = sesionRepository.obtenerSesion()
            if (sesion != null) {
                SesionUsuario.perfil = sesion.perfil
                if (sesion.usuarioRut != null) {
                    val usuario = usuarioRepository.obtenerPorRut(sesion.usuarioRut)
                    if (usuario != null) {
                        SesionUsuario.perfilUsuarioActual = usuario.toPerfilUsuario()
                    }
                }
            }
            _uiState.value = SesionUiState(cargando = false, perfil = sesion?.perfil)
        }
    }

    //Se llama cuando el login es exitoso
    fun iniciarSesion(perfil: String, usuario: Usuario? = null) {
        SesionUsuario.perfil = perfil
        if (usuario != null) {
            SesionUsuario.perfilUsuarioActual = usuario.toPerfilUsuario()
        }
        viewModelScope.launch {
            sesionRepository.guardar(perfil = perfil, usuarioRut = usuario?.rut)
        }
    }

    //Se llama al cerrar sesión
    fun cerrarSesion() {
        SesionUsuario.perfil = "Residente"
        SesionUsuario.perfilUsuarioActual = null
        viewModelScope.launch { sesionRepository.borrar() }
    }
}
