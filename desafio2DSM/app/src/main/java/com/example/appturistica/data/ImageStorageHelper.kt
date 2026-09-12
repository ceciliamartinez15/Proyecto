package com.example.appturistica.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object ImageStorageHelper {

    fun guardarImagenLocal(context: Context, uri: Uri): String? {
        return try {
            val inputStream =
                context.contentResolver.openInputStream(uri) ?: return null

            val tipoMime =
                context.contentResolver.getType(uri)

            val extension = when (tipoMime) {
                "image/png" -> ".png"
                "image/webp" -> ".webp"
                "image/jpeg" -> ".jpg"
                else -> ".jpg"
            }

            val nombreArchivo =
                "destino_${UUID.randomUUID()}$extension"

            val archivo = File(context.filesDir, nombreArchivo)

            inputStream.use { input ->
                archivo.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            archivo.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}