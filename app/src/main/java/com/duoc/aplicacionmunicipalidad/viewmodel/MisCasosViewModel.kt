package com.duoc.aplicacionmunicipalidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import com.duoc.aplicacionmunicipalidad.repository.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MisCasosUiState(
    val filtro: String = "Todos",            // Todos, En curso o Resueltos
    val esFuncionario: Boolean = false,
    val requerimientos: List<Requerimiento> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
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
    private val repository: RequerimientoRepository = RequerimientoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisCasosUiState())
    val uiState: StateFlow<MisCasosUiState> = _uiState.asStateFlow()

    // El residente descarga solo lo suyo; el funcionario descarga todos los casos
    fun cargar() {
        val funcionario = SesionUsuario.esFuncionario
        val rutResidente = SesionUsuario.perfilUsuarioActual?.rut
        _uiState.update { it.copy(esFuncionario = funcionario, cargando = true, error = null) }

        viewModelScope.launch {
            try {
                val lista = repository.listar(if (funcionario) null else rutResidente)
                _uiState.update { it.copy(requerimientos = lista, cargando = false) }
            } catch (error: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(error)) }
            }
        }
    }

    fun onFiltroChange(valor: String) = _uiState.update { it.copy(filtro = valor) }
}
