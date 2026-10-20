package com.example.mpaz_pro_2.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_2.model.ValidationEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherScreen(
    userName: String,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var maxAttempts by remember { mutableStateOf("3") }
    var evalDate by remember { mutableStateOf("2026-11-15") } // Campo Date
    var isGraded by remember { mutableStateOf(false) }         // Checkbox
    var isEnabled by remember { mutableStateOf(true) }         // Switch

    var titleError by remember { mutableStateOf<String?>(null) }
    var isSaved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Docente - MPAZ PRO") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("← Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bienvenido/a Docente: $userName 👨‍🏫",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Gestión de Evaluaciones y Parámetros para 2° Básico.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 1. Campo String: Título de OA
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = ValidationEngine.validateUserName(it)
                    isSaved = false
                },
                label = { Text("Objetivo de Aprendizaje (OA) [Text]") },
                placeholder = { Text("Ej: OA01 - Lectura Breve") },
                isError = titleError != null,
                trailingIcon = {
                    if (titleError != null) {
                        Text("⚠️", modifier = Modifier.padding(end = 8.dp))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = titleError != null) {
                titleError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // 2. Campos Int y Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = maxAttempts,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            maxAttempts = it
                            isSaved = false
                        }
                    },
                    label = { Text("Intentos [Int]") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = evalDate,
                    onValueChange = {
                        evalDate = it
                        isSaved = false
                    },
                    label = { Text("Fecha Plazo [Date]") },
                    placeholder = { Text("AAAA-MM-DD") },
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. Elemento Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isGraded,
                    onCheckedChange = { isGraded = it }
                )
                Text(
                    text = "Evaluación Sumativa (Con nota)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 4. Elemento Switch
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Habilitar evaluación para estudiantes",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it }
                    )
                }
            }

            // Alerta visual de éxito
            AnimatedVisibility(visible = isSaved) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✓")
                        Text("Configuración guardada para 2° Básico.")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 5. Elemento Button
            Button(
                onClick = {
                    val error = ValidationEngine.validateUserName(title)
                    titleError = error
                    if (error == null && title.isNotBlank()) {
                        isSaved = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar Configuración")
            }
        }
    }
}