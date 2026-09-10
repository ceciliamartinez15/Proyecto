package com.example.appturistica.data

import com.example.appturistica.model.Destino
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DestinoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("destinos")

    // Leer todos los destinos (Read)
    suspend fun obtenerDestinos(): List<Destino> {
        val snapshot = coleccion.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Destino::class.java)?.copy(id = doc.id)
        }
    }

    // Crear un nuevo destino (Create)
    suspend fun agregarDestino(destino: Destino): Boolean {
        return try {
            coleccion.add(destino).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Actualizar un destino existente (Update)
    suspend fun actualizarDestino(destino: Destino): Boolean {
        return try {
            coleccion.document(destino.id).set(destino).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Eliminar un destino (Delete)
    suspend fun eliminarDestino(id: String): Boolean {
        return try {
            coleccion.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}