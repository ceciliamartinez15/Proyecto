package com.example.appturistica.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appturistica.viewmodel.DestinoViewModel

sealed class Pantalla(val ruta: String) {
    object Login : Pantalla("login")
    object Catalogo : Pantalla("catalogo")
    object CrearDestino : Pantalla("crear_destino")
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val destinoViewModel: DestinoViewModel = viewModel()

    NavHost(navController = navController, startDestination = Pantalla.Login.ruta) {

        composable(Pantalla.Login.ruta) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Pantalla.Catalogo.ruta) {
                        popUpTo(Pantalla.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Pantalla.Catalogo.ruta) {
            CatalogoScreen(
                viewModel = destinoViewModel,
                onAgregarDestino = {
                    destinoViewModel.limpiarFormulario() // asegura modo "crear" limpio
                    navController.navigate(Pantalla.CrearDestino.ruta)
                },
                onEditarDestino = { destino ->
                    destinoViewModel.seleccionarDestino(destino)
                    navController.navigate(Pantalla.CrearDestino.ruta)
                }
            )
        }

        composable(Pantalla.CrearDestino.ruta) {
            val destinoSeleccionado by destinoViewModel.destinoSeleccionado.collectAsState()

            CrearDestinoScreen(
                viewModel = destinoViewModel,
                destinoAEditar = destinoSeleccionado,
                onDestinoCreado = {
                    navController.popBackStack()
                },
                onCancelar = {
                    destinoViewModel.limpiarFormulario()
                    navController.popBackStack()
                }
            )
        }
    }
}