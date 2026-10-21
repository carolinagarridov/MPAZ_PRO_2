package com.example.mpaz_pro_2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evaluations")
data class EvaluationEntity(
    @PrimaryKey val id: Int = 1, // ID fijo para mantener la evaluación activa
    val oaTitle: String,
    val maxAttempts: Int,
    val evalDate: String,
    val isGraded: Boolean,
    val isEnabled: Boolean
)