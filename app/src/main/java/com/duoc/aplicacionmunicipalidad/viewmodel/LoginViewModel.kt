package com.duoc.aplicacionmunicipalidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Definimos los posibles estados de nuestra pantalla de Login
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val userProfile: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    // Usamos StateFlow para que la vista (Jetpack Compose) reaccione a los cambios
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun performLogin(username: String, password: String) {
        // 1. Validación de campos vacíos
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Por favor, ingresa tu usuario y contraseña.")
            return
        }

        // 2. Cambiamos el estado a Cargando
        _loginState.value = LoginState.Loading

        // 3. Simulamos una conexión a un servicio con una corrutina
        viewModelScope.launch {
            delay(1500) // Simula 1.5 segundos de carga (delay de red)

            // 4. Validación con datos sintéticos para el MVP[cite: 2]
            when {
                username == "residente" && password == "1234" ->
                    _loginState.value = LoginState.Success("Residente")

                username == "operador" && password == "1234" ->
                    _loginState.value = LoginState.Success("Operador")

                username == "supervisor" && password == "1234" ->
                    _loginState.value = LoginState.Success("Supervisor")

                username == "admin" && password == "1234" ->
                    _loginState.value = LoginState.Success("Administrador")

                else ->
                    _loginState.value = LoginState.Error("Credenciales incorrectas. (Prueba con usuario 'residente' y clave '1234')")
            }
        }
    }

    // Permite reiniciar el estado, por ejemplo, después de mostrar un mensaje de error
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}