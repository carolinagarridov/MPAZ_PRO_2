package com.example.mpaz_pro_2.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.mpaz_pro_2.data.local.UserEntity
import com.example.mpaz_pro_2.ui.viewmodel.MainViewModel
import com.example.mpaz_pro_2.utils.ImageUtils

@Composable
fun StudentDetailDialog(
    student: UserEntity,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val bitmap = remember(key1 = student.profileImageUri) {
        student.profileImageUri?.let { ImageUtils.loadBitmapFromUri(context, it) }
    }

    val allAttempts by viewModel.allAttempts.collectAsState()
    val attempts = remember(key1 = allAttempts, key2 = student.username) {
        allAttempts
            .filter { it.studentUsername == student.username }
            .sortedByDescending { it.timestamp }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Foto de Perfil / Avatar

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = student.name.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@${student.username} • Rol: ${student.role}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Text(
                    text = "Historial de Intentos (${attempts.size}):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                if (attempts.isEmpty()) {
                    Text(
                        text = "El estudiante aún no ha realizado ningún intento.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(attempts) { attempt ->
                            ElevatedCard(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("OA: ${attempt.oaCode}", fontWeight = FontWeight.Bold)
                                        Text("Fecha: ${attempt.timestamp}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(
                                        "${attempt.score}%",
                                        fontWeight = FontWeight.Bold,
                                        color = if (attempt.score >= 60) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}