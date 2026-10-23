package com.example.mpaz_pro_2.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_2.data.local.UserEntity
import com.example.mpaz_pro_2.utils.ImageUtils

@Composable
fun UserProfileHeader(
    user: UserEntity?,
    fallbackName: String,
    fallbackRole: String
) {
    val context = LocalContext.current
    val bitmap = remember(key1 = user?.profileImageUri) {
        user?.profileImageUri?.let { ImageUtils.loadBitmapFromUri(context, it) }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar de Perfil o Inicial
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
            } else {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = (user?.name ?: fallbackName).take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Column {
                Text(
                    text = "Bienvenido/a: ${user?.name ?: fallbackName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rol: ${user?.role ?: fallbackRole}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}