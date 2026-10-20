package com.example.mpaz_pro_2
import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. Tabla de Usuarios (Docentes / Estudiantes de 2° Básico)
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: String, // "DOCENTE" o "ESTUDIANTE"
    val grade: String = "2° Básico"
)

// 2. Tabla de Progreso del Alumno
@Entity(tableName = "student_progress")
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val oaCode: String, // Código del Objetivo de Aprendizaje
    val scorePercentage: Float,
    val timestamp: String // Fecha y hora
)