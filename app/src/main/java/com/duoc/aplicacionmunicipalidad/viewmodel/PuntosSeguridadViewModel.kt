package com.duoc.aplicacionmunicipalidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.PuntoSeguridad
import com.duoc.aplicacionmunicipalidad.repository.PuntosSeguridadRepository
import com.duoc.aplicacionmunicipalidad.repository.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PuntosSeguridadUiState(
    val puntos: List<PuntoSeguridad> = emptyList(),
    val filtroTipo: String? = null,      // null = todos los tipos
    val cargando: Boolean = true,
    val error: String? = null
) {
    val visibles: List<PuntoSeguridad>
        get() = puntos.filter { filtroTipo == null || it.tipo == filtroTipo }
}

class PuntosSeguridadViewModel(
    private val repository: PuntosSeguridadRepository = PuntosSeguridadRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PuntosSeguridadUiState())
    val uiState: StateFlow<PuntosSeguridadUiState> = _uiState.asStateFlow()

    // Consulta la API externa; también sirve como "reintentar" si falló
    fun cargar() {
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            try {
                val lista = repository.obtenerPuntos()
                _uiState.update { it.copy(puntos = lista, cargando = false) }
            } catch (error: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(error)) }
            }
        }
    }

    fun onFiltroTipoChange(tipo: String?) = _uiState.update { it.copy(filtroTipo = tipo) }
}
