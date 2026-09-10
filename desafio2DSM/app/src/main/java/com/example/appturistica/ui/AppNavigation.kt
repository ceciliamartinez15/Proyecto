package com.example.appturistica.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Pantalla(val ruta: String) {
    object Login : Pantalla("login")
    object Catalogo : Pantalla("catalogo")
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

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
            CatalogoScreen()
        }
    }
}