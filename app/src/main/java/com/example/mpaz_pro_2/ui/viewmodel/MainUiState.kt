package com.example.mpaz_pro_2.ui.viewmodel

data class MainUiState(
    val name: String = "",
    val nameError: String? = null,
    val role: String = "ESTUDIANTE",
    val selectedImageUri: String? = null
)