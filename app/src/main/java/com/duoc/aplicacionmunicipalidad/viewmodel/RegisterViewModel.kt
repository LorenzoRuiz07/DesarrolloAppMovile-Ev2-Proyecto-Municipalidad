package com.duoc.aplicacionmunicipalidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aplicacionmunicipalidad.remote.RegistroSolicitud
import com.duoc.aplicacionmunicipalidad.repository.UsuarioRepository
import com.duoc.aplicacionmunicipalidad.repository.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val formatoCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun register(
        nombre: String,
        rut: String,
        correo: String,
        telefono: String,
        direccion: String,
        password: String,
        confirmarPassword: String,
        aceptaTerminos: Boolean
    ) {
        val error = validar(
            nombre, rut, correo, telefono, direccion, password, confirmarPassword, aceptaTerminos
        )
        if (error != null) {
            _registerState.value = RegisterState.Error(error)
            return
        }

        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            try {
                usuarioRepository.registrar(
                    RegistroSolicitud(
                        rut = rut.trim(),
                        nombre = nombre.trim(),
                        correo = correo.trim(),
                        telefono = telefono.trim(),
                        direccion = direccion.trim(),
                        password = password
                    )
                )
                _registerState.value = RegisterState.Success
            } catch (error: Exception) {
                _registerState.value = RegisterState.Error(mensajeDeError(error))
            }
        }
    }

    private fun validar(
        nombre: String,
        rut: String,
        correo: String,
        telefono: String,
        direccion: String,
        password: String,
        confirmarPassword: String,
        aceptaTerminos: Boolean
    ): String? {
        if (nombre.isBlank() || rut.isBlank() || correo.isBlank() ||
            password.isBlank() || confirmarPassword.isBlank()
        ) {
            return "Completa todos los campos obligatorios (*)."
        }
        if (nombre.trim().split(" ").filter { it.isNotBlank() }.size < 2) {
            return "Ingresa tu nombre completo (nombre y apellido)."
        }
        if (!rutValido(rut)) {
            return "El RUT no es válido. Ejemplo: 12.345.678-5"
        }
        if (!formatoCorreo.matches(correo.trim())) {
            return "El correo electrónico no es válido."
        }
        if (telefono.isNotBlank()) {
            val soloNumeros = telefono.filter { it.isDigit() }
            if (soloNumeros.length < 8 || soloNumeros.length > 11) {
                return "El teléfono no es válido."
            }
        }
        if (password.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres."
        }
        if (password != confirmarPassword) {
            return "Las contraseñas no coinciden."
        }
        if (!aceptaTerminos) {
            return "Debes aceptar los términos y condiciones."
        }
        return null
    }

    private fun rutValido(rut: String): Boolean {
        val limpio = rut.replace(".", "").replace("-", "").uppercase()
        if (limpio.length < 8 || limpio.length > 9) return false

        val cuerpo = limpio.dropLast(1)
        val digito = limpio.last()
        if (!cuerpo.all { it.isDigit() }) return false

        var suma = 0
        var multiplicador = 2
        for (c in cuerpo.reversed()) {
            suma += c.digitToInt() * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }
        val resto = 11 - (suma % 11)
        val esperado = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> resto.digitToChar()
        }
        return digito == esperado
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}
