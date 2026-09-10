package com.example.appturistica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appturistica.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val sesionIniciada: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun iniciarSesion(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Debes llenar todos los campos")
            return
        }

        _uiState.value = _uiState.value.copy(cargando = true, error = null)
        viewModelScope.launch {
            val resultado = repository.iniciarSesion(correo, contrasena)
            resultado.onSuccess {
                _uiState.value = _uiState.value.copy(cargando = false, sesionIniciada = true)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(cargando = false, error = e.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun registrar(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Debes llenar todos los campos")
            return
        }
        if (contrasena.length < 6) {
            _uiState.value = _uiState.value.copy(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        _uiState.value = _uiState.value.copy(cargando = true, error = null)
        viewModelScope.launch {
            val resultado = repository.registrar(correo, contrasena)
            resultado.onSuccess {
                _uiState.value = _uiState.value.copy(cargando = false, sesionIniciada = true)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(cargando = false, error = e.message ?: "Error al registrar")
            }
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}