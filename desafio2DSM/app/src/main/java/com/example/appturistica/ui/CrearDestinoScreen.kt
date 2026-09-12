package com.example.appturistica.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.appturistica.data.ImageStorageHelper
import com.example.appturistica.model.Destino
import com.example.appturistica.viewmodel.DestinoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearDestinoScreen(
    viewModel: DestinoViewModel = viewModel(),
    destinoAEditar: Destino? = null,
    onDestinoCreado: () -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    val formState by viewModel.formState.collectAsState()
    val esEdicion = destinoAEditar != null

    var nombre by remember { mutableStateOf(destinoAEditar?.nombre ?: "") }
    var pais by remember { mutableStateOf(destinoAEditar?.pais ?: "") }
    var precio by remember { mutableStateOf(destinoAEditar?.precio?.toString() ?: "") }
    var descripcion by remember { mutableStateOf(destinoAEditar?.descripcion ?: "") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var imagenLocalPath by remember { mutableStateOf(destinoAEditar?.imagenUrl) }
    var expandedSpinner by remember { mutableStateOf(false) }

    val paises = listOf("México", "Perú", "Francia", "España", "Italia", "Japón", "Estados Unidos", "Colombia", "Argentina", "Brasil")

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imagenUri = uri
            imagenLocalPath = ImageStorageHelper.guardarImagenLocal(context, uri)
        }
    }

    LaunchedEffect(formState.exito) {
        if (formState.exito) {
            viewModel.limpiarFormulario()
            onDestinoCreado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = if (esEdicion) "Editar destino" else "Nuevo destino",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Selector de imagen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            when {
                imagenUri != null -> {
                    AsyncImage(
                        model = imagenUri,
                        contentDescription = "Imagen del destino",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                !imagenLocalPath.isNullOrBlank() -> {
                    AsyncImage(
                        model = imagenLocalPath,
                        contentDescription = "Imagen del destino",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Toca para elegir una foto")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del destino") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expandedSpinner,
            onExpandedChange = { expandedSpinner = it }
        ) {
            OutlinedTextField(
                value = pais,
                onValueChange = {},
                readOnly = true,
                label = { Text("País") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSpinner) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedSpinner,
                onDismissRequest = { expandedSpinner = false }
            ) {
                paises.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            pais = opcion
                            expandedSpinner = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio del paquete (USD)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción (mínimo 20 caracteres)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            supportingText = { Text("${descripcion.length}/20 caracteres mínimo") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (formState.error != null) {
            Text(
                text = formState.error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (esEdicion) {
                    viewModel.actualizarDestino(
                        id = destinoAEditar!!.id,
                        nombre = nombre,
                        pais = pais,
                        precioTexto = precio,
                        descripcion = descripcion,
                        imagenLocalPath = imagenLocalPath
                    )
                } else {
                    viewModel.crearDestino(nombre, pais, precio, descripcion, imagenLocalPath)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !formState.guardando
        ) {
            if (formState.guardando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(if (esEdicion) "Actualizar destino" else "Guardar destino")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onCancelar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}