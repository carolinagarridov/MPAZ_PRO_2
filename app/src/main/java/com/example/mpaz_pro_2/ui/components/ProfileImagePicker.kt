package com.example.mpaz_pro_2.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_2.utils.ImageUtils
import androidx.compose.ui.graphics.asImageBitmap
@Composable
fun ProfileImagePicker(
    selectedImageUri: String?,
    onImageCapturedOrSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var loadedBitmap by remember(selectedImageUri) {
        mutableStateOf(ImageUtils.loadBitmapFromUri(context, selectedImageUri))
    }

    // Captura con Cámara Nativa
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val internalPath = ImageUtils.saveBitmapToInternalStorage(context, bitmap)
            loadedBitmap = bitmap.asImageBitmap()
            onImageCapturedOrSelected(internalPath)
        }
    }

    // Selección desde Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val internalPath = ImageUtils.saveUriToInternalStorage(context, uri)
            loadedBitmap = ImageUtils.loadBitmapFromUri(context, internalPath)
            onImageCapturedOrSelected(internalPath)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (loadedBitmap != null) {
                Image(
                    bitmap = loadedBitmap!!,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("👤", style = MaterialTheme.typography.headlineLarge)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { cameraLauncher.launch(null) },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("📷 Tomar Foto")
            }

            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("🖼️ Galería")
            }
        }
    }
}