package com.example.appturistica.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appturistica.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginExitoso: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var esRegistro by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.sesionIniciada) {
        if (uiState.sesionIniciada) {
            onLoginExitoso()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "AppTuristica",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (esRegistro)
                "Crea tu cuenta de agente"
            else
                "Inicia sesión",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = {
                Text("Correo electrónico")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = {
                Text("Contraseña")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.error != null) {

            Text(
                text = uiState.error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {

                if (esRegistro) {
                    viewModel.registrar(
                        correo,
                        contrasena
                    )
                } else {
                    viewModel.iniciarSesion(
                        correo,
                        contrasena
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.cargando
        ) {

            if (uiState.cargando) {

                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp)
                )

            } else {

                Text(
                    if (esRegistro)
                        "Registrarse"
                    else
                        "Iniciar sesión"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                esRegistro = !esRegistro
                viewModel.limpiarError()
            }
        ) {

            Text(
                if (esRegistro)
                    "¿Ya tienes cuenta? Inicia sesión"
                else
                    "¿No tienes cuenta? Regístrate"
            )
        }
    }
}