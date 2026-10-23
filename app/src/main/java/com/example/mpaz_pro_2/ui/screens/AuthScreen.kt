package com.example.mpaz_pro_2.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_2.data.local.UserEntity
import com.example.mpaz_pro_2.ui.components.ProfileImagePicker
import com.example.mpaz_pro_2.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: (UserEntity) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "MPAZ PRO 2.º",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Bienvenida
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Escuela Escritora Marcela Paz",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sistema Didáctico Interactivo para 2° Básico",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Pestañas de Selección (Login / Registro)
            TabRow(
                selectedTabIndex = if (uiState.isLoginTab) 0 else 1,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = uiState.isLoginTab,
                    onClick = { viewModel.onTabChanged(true) },
                    text = { Text("Iniciar Sesión", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = !uiState.isLoginTab,
                    onClick = { viewModel.onTabChanged(false) },
                    text = { Text("Registrarse", fontWeight = FontWeight.SemiBold) }
                )
            }

            // Campo Nombre de Usuario
            OutlinedTextField(
                value = uiState.usernameInput,
                onValueChange = { viewModel.onUsernameChanged(it) },
                label = { Text("Nombre de Usuario") },
                isError = uiState.usernameError != null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = uiState.usernameError != null) {
                uiState.usernameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            // Campos específicos para Registro
            if (!uiState.isLoginTab) {
                OutlinedTextField(
                    value = uiState.nameInput,
                    onValueChange = { viewModel.onNameChanged(it) },
                    label = { Text("Nombre Completo") },
                    isError = uiState.nameError != null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                AnimatedVisibility(visible = uiState.nameError != null) {
                    uiState.nameError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Text(
                    text = "Seleccione Perfil de Usuario:",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = uiState.roleInput == "ESTUDIANTE",
                        onClick = { viewModel.onRoleChanged("ESTUDIANTE") },
                        label = { Text("Estudiante") }
                    )
                    FilterChip(
                        selected = uiState.roleInput == "DOCENTE",
                        onClick = { viewModel.onRoleChanged("DOCENTE") },
                        label = { Text("Docente") }
                    )
                    FilterChip(
                        selected = uiState.roleInput == "UTP",
                        onClick = { viewModel.onRoleChanged("UTP") },
                        label = { Text("UTP") }
                    )
                }

                // Selector de Foto de Perfil
                Text(
                    text = "Foto de Perfil (Cámara o Galería):",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                ProfileImagePicker(
                    selectedImageUri = uiState.imageUriInput,
                    onImageCapturedOrSelected = { path ->
                        viewModel.onImageSelected(path)
                    }
                )
            }

            // Campo Contraseña
            OutlinedTextField(
                value = uiState.passwordInput,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "Ocultar" else "Mostrar")
                    }
                },
                isError = uiState.passwordError != null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = uiState.passwordError != null) {
                uiState.passwordError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            if (!uiState.isLoginTab) {
                OutlinedTextField(
                    value = uiState.confirmPasswordInput,
                    onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                    label = { Text("Confirmar Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = uiState.confirmPasswordError != null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                AnimatedVisibility(visible = uiState.confirmPasswordError != null) {
                    uiState.confirmPasswordError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Alerta de Error
            AnimatedVisibility(visible = uiState.generalError != null) {
                uiState.generalError?.let {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(it, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Principal
            Button(
                onClick = {
                    if (uiState.isLoginTab) {
                        viewModel.login(onSuccess = onAuthSuccess)
                    } else {
                        viewModel.register(onSuccess = onAuthSuccess)
                    }
                },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        if (uiState.isLoginTab) "Ingresar al Sistema" else "Crear Cuenta Cifrada",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}