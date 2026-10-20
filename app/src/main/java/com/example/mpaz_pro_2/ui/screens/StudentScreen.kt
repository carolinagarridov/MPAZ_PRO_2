package com.example.mpaz_pro_2.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_2.utils.AudioHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(
    userName: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val audioHelper = remember { AudioHelper(context) }

    var selectedAnswer by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }

    val questionText = "¿Cuál de las siguientes palabras es un sustantivo común?"

    DisposableEffect(Unit) {
        onDispose {
            audioHelper.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Módulo Estudiante - 2° Básico") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de bienvenida
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "¡Hola, $userName! 👋",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Curso: 2° Básico | Escuela Escritora Marcela Paz",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Desafío pedagógico
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lenguaje y Comunicación - OA01",
                            style = MaterialTheme.typography.labelLarge
                        )
                        IconButton(onClick = { audioHelper.speak(questionText) }) {
                            Text(text = "🔊", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = questionText,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    selectedAnswer = "Perro"
                    showResult = true
                    audioHelper.speak("¡Excelente trabajo! Respuesta correcta.")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("A) Perro")
            }

            OutlinedButton(
                onClick = {
                    selectedAnswer = "Correr"
                    showResult = true
                    audioHelper.speak("Inténtalo de nuevo. Correr es una acción.")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("B) Correr")
            }

            // Retroalimentación animada
            AnimatedVisibility(visible = showResult) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedAnswer == "Perro") MaterialTheme.colorScheme.tertiaryContainer
                        else MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedAnswer == "Perro") "¡Excelente trabajo $userName! Respuesta correcta."
                        else "Inténtalo de nuevo. 'Correr' es un verbo.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    audioHelper.stop()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}