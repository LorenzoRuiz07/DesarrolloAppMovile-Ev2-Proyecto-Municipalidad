package com.duoc.aplicacionmunicipalidad.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.model.SesionUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val perfil: PerfilUsuario,
    val notificaciones: Boolean = true,
    val mostrarDialogoSalir: Boolean = false
)

class ProfileViewModel : ViewModel() {

    //Datos de ejemplo
    private val _uiState = MutableStateFlow(
        ProfileUiState(
            perfil = PerfilUsuario(
                nombre = "María Pérez Soto",
                rut = "12.345.678-5",
                correo = "maria.perez@correo.cl",
                telefono = "+56 9 1234 5678",
                direccion = "Carmen 1234, San Joaquín",
                tipoPerfil = SesionUsuario.perfil
            )
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onNotificacionesChange(activadas: Boolean) =
        _uiState.update { it.copy(notificaciones = activadas) }

    fun onSalirClick() = _uiState.update { it.copy(mostrarDialogoSalir = true) }

    fun onDialogoDismiss() = _uiState.update { it.copy(mostrarDialogoSalir = false) }
    
    fun onGuardarPerfil(
        nombre: String,
        rut: String,
        correo: String,
        telefono: String,
        direccion: String
    ) {
        _uiState.update { state ->
            state.copy(
                perfil = state.perfil.copy(
                    nombre = nombre,
                    rut = rut,
                    correo = correo,
                    telefono = telefono,
                    direccion = direccion
                )
            )
        }
    }
}
