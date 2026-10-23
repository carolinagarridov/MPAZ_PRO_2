package com.example.mpaz_pro_2.model

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object ValidationEngine {

    fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "El nombre no puede estar vacío."
            name.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres."
            else -> null
        }
    }

    fun validateUsername(username: String): String? {
        val u = username.trim()
        return when {
            u.isBlank() -> "El usuario es requerido."
            u.length < 4 -> "El usuario debe tener al menos 4 caracteres."
            u.contains(" ") -> "El usuario no debe contener espacios."
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es requerida."
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
            else -> null
        }
    }

    fun validatePasswordConfirmation(password: String, confirm: String): String? {
        return if (password != confirm) "Las contraseñas no coinciden." else null
    }

    fun validateMaxAttempts(attemptsStr: String): String? {
        val attempts = attemptsStr.trim().toIntOrNull()
        return when {
            attempts == null -> "Ingrese un número válido."
            attempts !in 1..10 -> "Los intentos deben estar entre 1 y 10."
            else -> null
        }
    }

    /** Valida formato AAAA-MM-DD y que sea una fecha real de calendario. */
    fun validateDate(dateStr: String): String? {
        val value = dateStr.trim()
        if (!Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(value)) {
            return "Formato de fecha inválido (AAAA-MM-DD)."
        }
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            format.isLenient = false
            format.parse(value)
            null
        } catch (e: ParseException) {
            "La fecha no existe en el calendario."
        }
    }

    fun validateRequired(value: String, fieldName: String, minLength: Int = 1): String? {
        return when {
            value.isBlank() -> "$fieldName es obligatorio."
            value.trim().length < minLength -> "$fieldName debe tener al menos $minLength caracteres."
            else -> null
        }
    }

    /** Código de OA con formato OA01, OA12, etc., y sin repetirse. */
    fun validateOaCode(code: String, existingCodes: List<String>): String? {
        val c = code.trim().uppercase()
        return when {
            c.isBlank() -> "El código es obligatorio."
            !Regex("^OA\\d{2,3}$").matches(c) -> "Use el formato OA05 (OA + 2 o 3 dígitos)."
            existingCodes.any { it.equals(c, ignoreCase = true) } -> "Ya existe un OA con el código $c."
            else -> null
        }
    }

    /** La alternativa correcta debe tener texto. */
    fun validateCorrectOption(correct: String, optionA: String, optionB: String, optionC: String): String? {
        val selected = when (correct) {
            "A" -> optionA
            "B" -> optionB
            "C" -> optionC
            else -> ""
        }
        return if (selected.isBlank()) "La alternativa correcta ($correct) no puede estar vacía." else null
    }
}