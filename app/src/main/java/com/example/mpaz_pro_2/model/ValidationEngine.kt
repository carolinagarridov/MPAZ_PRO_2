package com.example.mpaz_pro_2.model

object ValidationEngine {
    fun validateUserName(name: String): String? {
        return when {
            name.isBlank() -> "El nombre no puede estar vacío."
            name.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres."
            else -> null
        }
    }
    fun validateRole(role: String): String? {
        return if (role.isBlank()) "Debe seleccionar un rol válido." else null
    }
}