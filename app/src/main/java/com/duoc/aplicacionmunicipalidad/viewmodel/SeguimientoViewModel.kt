package com.duoc.aplicacionmunicipalidad.viewmodel


import androidx.lifecycle.ViewModel
import com.duoc.aplicacionmunicipalidad.model.Etapa
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.model.construirEtapas
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SeguimientoUiState(
    val requerimiento: Requerimiento? = null,   // null = no se encontró
    val etapas: List<Etapa> = emptyList(),
    val avisosActivados: Boolean = true
)

class SeguimientoViewModel(
    private val repository: RequerimientoRepository = RequerimientoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeguimientoUiState())
    val uiState: StateFlow<SeguimientoUiState> = _uiState.asStateFlow()

    // Busca el requerimiento por su identificador, ej: SJ-2026-0148
    fun cargar(id: String) {
        val r = repository.obtenerRequerimientos().firstOrNull { it.id == id }
        _uiState.update {
            it.copy(
                requerimiento = r,
                etapas = if (r != null) construirEtapas(r) else emptyList()
            )
        }
    }

    fun onAvisosClick() = _uiState.update { it.copy(avisosActivados = !it.avisosActivados) }
}
