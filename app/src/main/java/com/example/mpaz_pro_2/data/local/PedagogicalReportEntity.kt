package com.example.mpaz_pro_2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mpaz_pro_2.utils.DateUtils

@Entity(tableName = "pedagogical_reports")
data class PedagogicalReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentUsername: String,
    val studentName: String = "",
    val subject: String = "",
    val oaCode: String = "",
    val globalScore: Int = 0,
    val strengths: String = "",
    val reinforcementAreas: String = "",
    val teacherObservations: String = "",
    val validationDate: String = DateUtils.today()
)