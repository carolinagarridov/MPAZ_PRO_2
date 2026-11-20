package com.example.mpaz_pro_2.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.mpaz_pro_2.utils.DateUtils

@Entity(
    tableName = "curricular_oas",
    indices = [Index(value = ["oaCode"], unique = true)]
)
data class CurricularOAEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String, // "Lenguaje y Comunicación", "Matemática", "Ciencias Naturales", "Historia, Geografía y C. Sociales"
    val oaCode: String,
    val oaTitle: String,
    val unit: String = "Unidad 1",
    val eje: String = "Eje Curricular Base",
    val description: String,
    val status: String = "BORRADOR", // "BORRADOR", "EN_REVISION", "AUTORIZADO", "PUBLICADO", "DESHABILITADO"
    val utpResponsible: String = "Jefatura UTP",
    val lastModified: String = DateUtils.now()
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val oaCode: String,
    val questionText: String,
    val challengeType: String = "SELECCION",
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val correctAnswer: String, // "A", "B", "C"
    val explanation: String
)

// Un único registro de configuración por OA: el índice único hace que REPLACE sobrescriba.
@Entity(
    tableName = "evaluation_configs",
    indices = [Index(value = ["oaCode"], unique = true)]
)
data class EvaluationConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val oaCode: String,
    val maxAttempts: Int = 3,
    val startDate: String = DateUtils.today(),
    val endDate: String = "2026-11-30",
    val isGraded: Boolean = true,
    val isEnabled: Boolean = true,
    val teacherName: String = "Docente Marcela Paz"
)

@Entity(tableName = "student_attempts")
data class StudentAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentUsername: String,
    val oaCode: String,
    val attemptNumber: Int,
    val score: Int,
    val totalQuestions: Int = 2,
    val correctAnswersCount: Int,
    val timestamp: String, // "YYYY-MM-DD HH:MM"
    val answersSummary: String
)