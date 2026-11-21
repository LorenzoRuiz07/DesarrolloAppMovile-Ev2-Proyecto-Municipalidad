package com.duoc.aplicacionmunicipalidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.Etapa
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.model.construirEtapas
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SeguimientoUiState(
    val requerimiento: Requerimiento? = null,   // null = no se encontró
    val etapas: List<Etapa> = emptyList(),
    val avisosActivados: Boolean = true,
    val cargando: Boolean = true
)

class SeguimientoViewModel(
    private val repository: RequerimientoRepository = RequerimientoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeguimientoUiState())
    val uiState: StateFlow<SeguimientoUiState> = _uiState.asStateFlow()

    // Busca el requerimiento en el servidor por su identificador, ej: SJ-2026-0148
    fun cargar(id: String) {
        _uiState.update { it.copy(cargando = true) }
        viewModelScope.launch {
            val encontrado = try {
                repository.obtener(id)
            } catch (error: Exception) {
                null
            }
            _uiState.update {
                it.copy(
                    requerimiento = encontrado,
                    etapas = if (encontrado != null) construirEtapas(encontrado) else emptyList(),
                    cargando = false
                )
            }
        }
    }

    fun onAvisosClick() = _uiState.update { it.copy(avisosActivados = !it.avisosActivados) }
}
