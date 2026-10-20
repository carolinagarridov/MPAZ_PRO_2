package com.example.mpaz_pro_2.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpaz_pro_2.ui.components.GalleryPicker
import com.example.mpaz_pro_2.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateToTeacher: (String) -> Unit = {},  // <-- Debe aceptar String
    onNavigateToStudent: (String) -> Unit = {}   // <-- Debe aceptar String
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "MPAZ PRO 2.º - Registro",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChanged(it) },
            label = { Text("Nombre del usuario") },
            isError = uiState.nameError != null,
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = uiState.nameError != null) {
            uiState.nameError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = uiState.role == "ESTUDIANTE",
                onClick = { viewModel.onRoleChanged("ESTUDIANTE") },
                label = { Text("Estudiante (2° Básico)") }
            )
            FilterChip(
                selected = uiState.role == "DOCENTE",
                onClick = { viewModel.onRoleChanged("DOCENTE") },
                label = { Text("Docente") }
            )
        }

        GalleryPicker(
            selectedImageUri = uiState.selectedImageUri,
            onImageSelected = { uri -> viewModel.onImageSelected(uri) }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.registerUser {
                    val registeredName = uiState.name.ifBlank { "Usuario" }
                    if (uiState.role == "DOCENTE") {
                        onNavigateToTeacher(registeredName)
                    } else {
                        onNavigateToStudent(registeredName)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar / Registrar")
        }
    }
}