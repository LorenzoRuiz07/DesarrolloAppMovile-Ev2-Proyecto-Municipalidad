package com.duoc.aplicacionmunicipalidad.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import com.duoc.aplicacionmunicipalidad.repository.RequerimientoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


data class Evidencia(
    val nombre: String,
    val detalle: String
)

data class NuevoRequerimientoUiState(
    val categoria: String = "",
    val tipo: String = "",
    val fechaMillis: Long = System.currentTimeMillis(),
    val lugar: String = "",
    val prioridad: String = "Media",
    val descripcion: String = "",
    val evidencia: Evidencia? = null,
    val enviando: Boolean = false,
    val error: String? = null,
    val idEnviado: String? = null
) {

    val fechaTexto: String
        get() = SimpleDateFormat("d MMM yyyy", Locale("es", "CL"))
            .format(fechaMillis).replace(".", "")

    val horaTexto: String
        get() = SimpleDateFormat("HH:mm", Locale("es", "CL")).format(fechaMillis)


    val hora: Int
        get() = Calendar.getInstance().apply { timeInMillis = fechaMillis }
            .get(Calendar.HOUR_OF_DAY)

    val minuto: Int
        get() = Calendar.getInstance().apply { timeInMillis = fechaMillis }
            .get(Calendar.MINUTE)
}

class NuevoRequerimientoViewModel(
    private val repository: RequerimientoRepository = RequerimientoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuevoRequerimientoUiState())
    val uiState: StateFlow<NuevoRequerimientoUiState> = _uiState.asStateFlow()

    fun onCategoriaChange(valor: String) =
        _uiState.update { it.copy(categoria = valor, tipo = "", error = null) }

    fun onTipoChange(valor: String) =
        _uiState.update { it.copy(tipo = valor, error = null) }


    fun onFechaSeleccionada(utcMillis: Long) {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = utcMillis
        }
        _uiState.update {
            val local = Calendar.getInstance().apply {
                timeInMillis = it.fechaMillis
                set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH))
            }
            it.copy(fechaMillis = local.timeInMillis, error = null)
        }
    }

    fun onHoraSeleccionada(hora: Int, minuto: Int) {
        _uiState.update {
            val local = Calendar.getInstance().apply {
                timeInMillis = it.fechaMillis
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minuto)
                set(Calendar.SECOND, 0)
            }
            it.copy(fechaMillis = local.timeInMillis, error = null)
        }
    }

    fun onLugarChange(valor: String) =
        _uiState.update { it.copy(lugar = valor, error = null) }


    fun onUsarUbicacion() =
        _uiState.update { it.copy(lugar = "Cerca de mi ubicación actual", error = null) }

    fun onPrioridadChange(valor: String) =
        _uiState.update { it.copy(prioridad = valor) }

    fun onDescripcionChange(valor: String) {
        if (valor.length <= 500) {
            _uiState.update { it.copy(descripcion = valor, error = null) }
        }
    }


    fun onAdjuntarEvidencia() {
        val ahora = System.currentTimeMillis()
        val nombre = "evidencia_" + SimpleDateFormat("ddMMyy_HHmm", Locale("es", "CL")).format(ahora) + ".jpg"
        val detalle = SimpleDateFormat("d MMM · HH:mm", Locale("es", "CL"))
            .format(ahora).replace(".", "") + " · 1,8 MB"
        _uiState.update { it.copy(evidencia = Evidencia(nombre, detalle)) }
    }

    fun onQuitarEvidencia() = _uiState.update { it.copy(evidencia = null) }

    fun enviar() {
        val s = _uiState.value

        val error = validar(s)
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }

        _uiState.update { it.copy(error = null, enviando = true) }


        viewModelScope.launch {
            delay(1200)
            val id = repository.generarId()
            repository.agregar(
                Requerimiento(
                    id = id,
                    titulo = "${s.tipo} en ${s.lugar.trim()}",
                    lugar = s.lugar.trim(),
                    sector = "San Joaquín",
                    categoria = s.categoria,
                    prioridad = s.prioridad,
                    estado = "Recibido",
                    fechaMillis = s.fechaMillis,
                    tipo = s.tipo,
                    descripcion = s.descripcion.trim(),
                    evidenciaNombre = s.evidencia?.nombre ?: "",
                    evidenciaDetalle = s.evidencia?.detalle ?: "",
                    esMio = true
                )
            )
            _uiState.update { it.copy(enviando = false, idEnviado = id) }
        }
    }


    private fun validar(s: NuevoRequerimientoUiState): String? {
        if (s.categoria.isBlank() || s.tipo.isBlank()) {
            return "Selecciona la categoría y el tipo de situación."
        }
        if (s.fechaMillis > System.currentTimeMillis() + 60_000) {
            return "La fecha y hora no pueden ser futuras."
        }
        if (s.lugar.trim().length < 3) {
            return "Indica dónde ocurre (calle, cruce o punto de referencia)."
        }
        if (s.descripcion.trim().length < 10) {
            return "Describe lo que ocurre (mínimo 10 caracteres)."
        }
        return null
    }
}
