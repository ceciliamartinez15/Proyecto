package com.example.appturistica.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    // Usuario actual (null si no ha iniciado sesión)
    val usuarioActual: FirebaseUser?
        get() = auth.currentUser

    // Registro de nuevo usuario
    suspend fun registrar(correo: String, contrasena: String): Result<FirebaseUser> {
        return try {
            val resultado = auth.createUserWithEmailAndPassword(correo, contrasena).await()
            val usuario = resultado.user
            if (usuario != null) {
                Result.success(usuario)
            } else {
                Result.failure(Exception("No se pudo crear el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Inicio de sesión
    suspend fun iniciarSesion(correo: String, contrasena: String): Result<FirebaseUser> {
        return try {
            val resultado = auth.signInWithEmailAndPassword(correo, contrasena).await()
            val usuario = resultado.user
            if (usuario != null) {
                Result.success(usuario)
            } else {
                Result.failure(Exception("No se pudo iniciar sesión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cerrar sesión
    fun cerrarSesion() {
        auth.signOut()
    }
}