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

data class FormularioUiState(
    val guardando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false
)

class DestinoViewModel : ViewModel() {

    private val repository = DestinoRepository()

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState

    private val _formState = MutableStateFlow(FormularioUiState())
    val formState: StateFlow<FormularioUiState> = _formState

    // Destino seleccionado para editar
    private val _destinoSeleccionado = MutableStateFlow<Destino?>(null)
    val destinoSeleccionado: StateFlow<Destino?> = _destinoSeleccionado

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

    fun seleccionarDestino(destino: Destino) {
        _destinoSeleccionado.value = destino
    }

    fun crearDestino(
        nombre: String,
        pais: String,
        precioTexto: String,
        descripcion: String,
        imagenLocalPath: String?
    ) {
        if (nombre.isBlank() || pais.isBlank() || precioTexto.isBlank() || descripcion.isBlank()) {
            _formState.value = _formState.value.copy(error = "Todos los campos son obligatorios")
            return
        }

        val precio = precioTexto.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            _formState.value = _formState.value.copy(error = "El precio debe ser un número mayor a 0")
            return
        }

        if (descripcion.length < 20) {
            _formState.value = _formState.value.copy(error = "La descripción debe tener al menos 20 caracteres")
            return
        }

        if (imagenLocalPath == null) {
            _formState.value = _formState.value.copy(error = "Debes seleccionar una imagen para el destino")
            return
        }

        _formState.value = _formState.value.copy(guardando = true, error = null)

        val nuevoDestino = Destino(
            nombre = nombre,
            pais = pais,
            precio = precio,
            descripcion = descripcion,
            imagenUrl = imagenLocalPath
        )

        viewModelScope.launch {
            val exito = repository.agregarDestino(nuevoDestino)
            if (exito) {
                _formState.value = _formState.value.copy(guardando = false, exito = true)
                cargarDestinos()
            } else {
                _formState.value = _formState.value.copy(guardando = false, error = "Error al guardar el destino")
            }
        }
    }

    fun actualizarDestino(
        id: String,
        nombre: String,
        pais: String,
        precioTexto: String,
        descripcion: String,
        imagenLocalPath: String?
    ) {
        if (nombre.isBlank() || pais.isBlank() || precioTexto.isBlank() || descripcion.isBlank()) {
            _formState.value = _formState.value.copy(error = "Todos los campos son obligatorios")
            return
        }

        val precio = precioTexto.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            _formState.value = _formState.value.copy(error = "El precio debe ser un número mayor a 0")
            return
        }

        if (descripcion.length < 20) {
            _formState.value = _formState.value.copy(error = "La descripción debe tener al menos 20 caracteres")
            return
        }

        if (imagenLocalPath == null) {
            _formState.value = _formState.value.copy(error = "Debes seleccionar una imagen para el destino")
            return
        }

        _formState.value = _formState.value.copy(guardando = true, error = null)

        val destinoActualizado = Destino(
            id = id,
            nombre = nombre,
            pais = pais,
            precio = precio,
            descripcion = descripcion,
            imagenUrl = imagenLocalPath
        )

        viewModelScope.launch {
            val exito = repository.actualizarDestino(destinoActualizado)
            if (exito) {
                _formState.value = _formState.value.copy(guardando = false, exito = true)
                cargarDestinos()
            } else {
                _formState.value = _formState.value.copy(guardando = false, error = "Error al actualizar el destino")
            }
        }
    }

    fun eliminarDestino(id: String) {
        viewModelScope.launch {
            repository.eliminarDestino(id)
            cargarDestinos()
        }
    }

    fun limpiarFormulario() {
        _formState.value = FormularioUiState()
        _destinoSeleccionado.value = null
    }
}