package com.example.mpaz_pro_2.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mpaz_pro_2.model.ValidationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Modelo del estado del formulario
data class MainUiState(
    val name: String = "",
    val nameError: String? = null,
    val role: String = "ESTUDIANTE",
    val selectedImageUri: String? = null
)
