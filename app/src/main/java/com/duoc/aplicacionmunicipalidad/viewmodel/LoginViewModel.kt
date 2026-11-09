package com.duoc.aplicacionmunicipalidad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.model.AppDatabase
import com.duoc.aplicacionmunicipalidad.model.Usuario
import com.duoc.aplicacionmunicipalidad.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val userProfile: String, val usuario: Usuario) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository = UsuarioRepository(
        AppDatabase.obtener(application).usuarioDao()
    )

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun performLogin(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Por favor, ingresa tu usuario y contraseña.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            delay(500)
            val usuarioEncontrado = usuarioRepository.buscarUsuario(username)
            if (usuarioEncontrado != null && usuarioEncontrado.password == password) {
                _loginState.value = LoginState.Success(
                    userProfile = usuarioEncontrado.tipoPerfil,
                    usuario = usuarioEncontrado
                )
            } else {
                _loginState.value = LoginState.Error("Credenciales incorrectas. (Prueba con tu usuario/RUT o 'residente' y clave '1234')")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
