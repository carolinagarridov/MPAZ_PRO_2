package com.example.mpaz_pro_2.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    private const val MAX_SIZE_PX = 512

    // Guarda el Bitmap tomado con la cámara en el almacenamiento interno
    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String {
        val file = File(context.filesDir, "profile_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return Uri.fromFile(file).toString()
    }

    // Copia la imagen seleccionada desde la Galería al almacenamiento interno (reducida)
    fun saveUriToInternalStorage(context: Context, uri: Uri): String {
        return try {
            val bitmap = decodeSampled(context, uri) ?: return uri.toString()
            saveBitmapToInternalStorage(context, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            uri.toString()
        }
    }

    // Carga la foto desde la ruta guardada en Room
    fun loadBitmapFromUri(context: Context, uriString: String?): ImageBitmap? {
        if (uriString.isNullOrBlank()) return null
        return try {
            decodeSampled(context, Uri.parse(uriString))?.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Decodifica reduciendo el tamaño para evitar OutOfMemory con fotos grandes
    private fun decodeSampled(context: Context, uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        }
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= MAX_SIZE_PX && bounds.outHeight / (sample * 2) >= MAX_SIZE_PX) {
            sample *= 2
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }
}