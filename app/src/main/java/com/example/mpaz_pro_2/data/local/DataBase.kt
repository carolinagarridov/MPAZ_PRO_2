package com.example.mpaz_pro_2.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_progress")
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val oaCode: String, // Código del Objetivo de Aprendizaje
    val scorePercentage: Float,
    val timestamp: String // Fecha y hora
)