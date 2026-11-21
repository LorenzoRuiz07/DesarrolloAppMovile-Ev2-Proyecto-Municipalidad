package com.duoc.aplicacionmunicipalidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.PerfilUsuario
import com.duoc.aplicacionmunicipalidad.repository.UsuarioRepository
import com.duoc.aplicacionmunicipalidad.repository.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val userProfile: String, val usuario: PerfilUsuario) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun performLogin(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Por favor, ingresa tu usuario y contraseña.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val usuario = usuarioRepository.iniciarSesion(username, password)
                _loginState.value = LoginState.Success(
                    userProfile = usuario.tipoPerfil,
                    usuario = usuario
                )
            } catch (error: Exception) {
                _loginState.value = LoginState.Error(mensajeDeError(error))
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
