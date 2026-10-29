package com.duoc.aplicacionmunicipalidad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.AppDatabase
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.repository.SesionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SesionUiState(
    val cargando: Boolean = true,
    val perfil: String? = null
)

class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SesionRepository(
        AppDatabase.obtener(application).sesionDao()
    )

    private val _uiState = MutableStateFlow(SesionUiState())
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    init {
        //Al abrir la app, busca si quedó una sesión guardada
        viewModelScope.launch {
            val perfil = repository.obtenerPerfil()
            if (perfil != null) {
                SesionUsuario.perfil = perfil
            }
            _uiState.value = SesionUiState(cargando = false, perfil = perfil)
        }
    }

    //Se llama cuando el login es exitoso
    fun iniciarSesion(perfil: String) {
        SesionUsuario.perfil = perfil
        viewModelScope.launch { repository.guardar(perfil) }
    }

    //Se llama al cerrar sesión
    fun cerrarSesion() {
        SesionUsuario.perfil = "Residente"
        viewModelScope.launch { repository.borrar() }
    }
}