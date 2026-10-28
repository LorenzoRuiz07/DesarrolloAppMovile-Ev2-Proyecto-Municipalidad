package com.duoc.aplicacionmunicipalidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MisCasosUiState(
    val filtro: String = "Todos",            // Todos, En curso o Resueltos
    val esFuncionario: Boolean = false,
    val requerimientos: List<Requerimiento> = emptyList()
) {
    val visibles: List<Requerimiento>
        get() = requerimientos
            .filter {
                when (filtro) {
                    "En curso" -> it.estado != "Resuelto"
                    "Resueltos" -> it.estado == "Resuelto"
                    else -> true
                }
            }
            .sortedByDescending { it.fechaMillis }
}

class MisCasosViewModel(
    repository: RequerimientoRepository = RequerimientoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisCasosUiState())
    val uiState: StateFlow<MisCasosUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.requerimientos.collect { lista ->
                val funcionario = SesionUsuario.esFuncionario
                _uiState.update {
                    it.copy(
                        esFuncionario = funcionario,
                        // El residente ve solo lo suyo; el funcionario ve todos los casos
                        requerimientos = if (funcionario) lista else lista.filter { r -> r.esMio }
                    )
                }
            }
        }
    }

    fun onFiltroChange(valor: String) = _uiState.update { it.copy(filtro = valor) }
}