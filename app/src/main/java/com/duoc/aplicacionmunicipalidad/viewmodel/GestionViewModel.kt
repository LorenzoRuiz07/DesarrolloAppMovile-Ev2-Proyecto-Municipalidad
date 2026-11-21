package com.duoc.aplicacionmunicipalidad.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.Etapa
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import com.duoc.aplicacionmunicipalidad.model.construirEtapas
import com.duoc.aplicacionmunicipalidad.model.unidadResponsable
import com.duoc.aplicacionmunicipalidad.remote.GestionSolicitud
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import com.duoc.aplicacionmunicipalidad.repository.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GestionUiState(
    val requerimiento: Requerimiento? = null,   // null = no se encontró
    val etapas: List<Etapa> = emptyList(),
    // Valores del formulario (se aplican recién al guardar)
    val estado: String = "",
    val unidad: String = "",
    val derivadoA: String = "",
    val observacion: String = "",
    val visibleParaResidente: Boolean = false,
    val error: String? = null,
    val guardado: Boolean = false,
    val cargando: Boolean = true,
    val eliminado: Boolean = false
)

class GestionViewModel(
    private val repository: RequerimientoRepository = RequerimientoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GestionUiState())
    val uiState: StateFlow<GestionUiState> = _uiState.asStateFlow()

    // Descarga el requerimiento del servidor y rellena el formulario con sus valores actuales
    fun cargar(id: String) {
        _uiState.update { it.copy(cargando = true) }
        viewModelScope.launch {
            val r = try {
                repository.obtener(id)
            } catch (error: Exception) {
                null
            }
            _uiState.update {
                if (r == null) {
                    it.copy(requerimiento = null, cargando = false)
                } else {
                    it.copy(
                        requerimiento = r,
                        etapas = construirEtapas(r),
                        estado = r.estado,
                        unidad = unidadResponsable(r),
                        derivadoA = r.derivadoA.ifBlank { SesionUsuario.perfil },
                        observacion = r.observacion,
                        visibleParaResidente = r.observacionVisible,
                        cargando = false
                    )
                }
            }
        }
    }

    fun onEstadoChange(valor: String) =
        _uiState.update { it.copy(estado = valor, guardado = false, error = null) }

    fun onUnidadChange(valor: String) =
        _uiState.update { it.copy(unidad = valor, guardado = false, error = null) }

    fun onDerivadoChange(valor: String) =
        _uiState.update { it.copy(derivadoA = valor, guardado = false, error = null) }

    fun onObservacionChange(valor: String) {
        if (valor.length <= 500) {
            _uiState.update { it.copy(observacion = valor, guardado = false, error = null) }
        }
    }

    fun onVisibleChange(valor: Boolean) =
        _uiState.update { it.copy(visibleParaResidente = valor, guardado = false) }

    fun guardar() {
        val s = _uiState.value
        val r = s.requerimiento ?: return

        // Para resolver un caso se debe dejar constancia del resultado
        if (s.estado == "Resuelto" && s.observacion.isBlank()) {
            _uiState.update {
                it.copy(error = "Agrega una observación con el resultado de la intervención.")
            }
            return
        }

        viewModelScope.launch {
            try {
                val actualizado = repository.gestionar(
                    r.id,
                    GestionSolicitud(
                        estado = s.estado,
                        unidad = s.unidad,
                        derivadoA = s.derivadoA,
                        observacion = s.observacion.trim(),
                        observacionVisible = s.visibleParaResidente
                    )
                )
                // El historial se actualiza con el nuevo estado
                _uiState.update {
                    it.copy(
                        requerimiento = actualizado,
                        etapas = construirEtapas(actualizado),
                        guardado = true,
                        error = null
                    )
                }
            } catch (error: Exception) {
                _uiState.update { it.copy(error = mensajeDeError(error)) }
            }
        }
    }

    // Solo el administrador puede eliminar un requerimiento (la pantalla muestra el botón según el perfil)
    fun eliminar() {
        val r = _uiState.value.requerimiento ?: return
        viewModelScope.launch {
            try {
                repository.eliminar(r.id)
                _uiState.update { it.copy(eliminado = true, error = null) }
            } catch (error: Exception) {
                _uiState.update { it.copy(error = mensajeDeError(error)) }
            }
        }
    }
}
