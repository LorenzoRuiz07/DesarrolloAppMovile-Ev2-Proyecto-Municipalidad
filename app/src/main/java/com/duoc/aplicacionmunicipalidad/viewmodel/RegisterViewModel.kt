package com.duoc.aplicacionmunicipalidad.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados posibles de la pantalla de registro
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {

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
        //Validamos los datos
        val error = validar(
            nombre, rut, correo, telefono, password, confirmarPassword, aceptaTerminos
        )
        if (error != null) {
            _registerState.value = RegisterState.Error(error)
            return
        }

        //Cambiamos el estado a Cargando
        _registerState.value = RegisterState.Loading

        //Simulamos el envío a un servicio (por ahora no guarda el usuario)
        viewModelScope.launch {
            delay(1500)
            _registerState.value = RegisterState.Success
        }
    }

    //Devuelve el mensaje de error o null si esta correcto
    private fun validar(
        nombre: String,
        rut: String,
        correo: String,
        telefono: String,
        password: String,
        confirmarPassword: String,
        aceptaTerminos: Boolean
    ): String? {
        if (nombre.isBlank() || rut.isBlank() || correo.isBlank() ||
            password.isBlank() || confirmarPassword.isBlank()
        ) {
            return "Completa todos los campos obligatorios (*)."
        }
        //Nombre y apellido, al menos dos palabras
        if (nombre.trim().split(" ").filter { it.isNotBlank() }.size < 2) {
            return "Ingresa tu nombre completo (nombre y apellido)."
        }
        if (!rutValido(rut)) {
            return "El RUT no es válido. Ejemplo: 12.345.678-5"
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()) {
            return "El correo electrónico no es válido."
        }
        // El teléfono es opcional, pero si lo escribe debe ser razonable
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

    //Validación del RUT chileno (módulo 11)
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

    //Reinicia el estado, por ejemplo después de mostrar un error
    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}