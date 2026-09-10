package com.example.appturistica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appturistica.data.DestinoRepository
import com.example.appturistica.model.Destino
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CatalogoUiState(
    val cargando: Boolean = false,
    val destinos: List<Destino> = emptyList(),
    val error: String? = null
)

class DestinoViewModel : ViewModel() {

    private val repository = DestinoRepository()

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState

    init {
        cargarDestinos()
    }

    fun cargarDestinos() {
        _uiState.value = _uiState.value.copy(cargando = true, error = null)
        viewModelScope.launch {
            try {
                val lista = repository.obtenerDestinos()
                _uiState.value = _uiState.value.copy(cargando = false, destinos = lista)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(cargando = false, error = e.message ?: "Error al cargar destinos")
            }
        }
    }
}