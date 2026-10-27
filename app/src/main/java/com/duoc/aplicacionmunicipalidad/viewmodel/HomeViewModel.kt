package com.duoc.aplicacionmunicipalidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val consulta: String = "",
    val verMapa: Boolean = false,
    val filtroEstado: String? = null,
    val filtroCategoria: String? = null,
    val filtroPrioridad: String? = null,
    val requerimientos: List<Requerimiento> = emptyList()
) {

    val visibles: List<Requerimiento>
        get() = requerimientos
            .filter { r ->
                consulta.isBlank() ||
                        r.titulo.contains(consulta, ignoreCase = true) ||
                        r.lugar.contains(consulta, ignoreCase = true) ||
                        r.sector.contains(consulta, ignoreCase = true) ||
                        r.id.contains(consulta, ignoreCase = true)
            }
            .filter { filtroEstado == null || it.estado == filtroEstado }
            .filter { filtroCategoria == null || it.categoria == filtroCategoria }
            .filter { filtroPrioridad == null || it.prioridad == filtroPrioridad }
            .sortedByDescending { it.fechaMillis }
}

class HomeViewModel(
    private val repository: RequerimientoRepository = RequerimientoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {

        viewModelScope.launch {
            repository.requerimientos.collect { lista ->
                _uiState.update { it.copy(requerimientos = lista) }
            }
        }
    }

    fun onConsultaChange(valor: String) = _uiState.update { it.copy(consulta = valor) }

    fun onVerMapaChange(valor: Boolean) = _uiState.update { it.copy(verMapa = valor) }

    fun onEstadoChange(valor: String?) = _uiState.update { it.copy(filtroEstado = valor) }

    fun onCategoriaChange(valor: String?) = _uiState.update { it.copy(filtroCategoria = valor) }

    fun onPrioridadChange(valor: String?) = _uiState.update { it.copy(filtroPrioridad = valor) }
}