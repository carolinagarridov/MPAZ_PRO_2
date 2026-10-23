package com.example.mpaz_pro_2.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurricularDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOA(oa: CurricularOAEntity)

    @Update
    suspend fun updateOA(oa: CurricularOAEntity)

    @Query("SELECT * FROM curricular_oas")
    fun getAllOAs(): Flow<List<CurricularOAEntity>>

    @Query("SELECT * FROM curricular_oas WHERE status IN ('AUTORIZADO', 'PUBLICADO')")
    fun getAuthorizedOAs(): Flow<List<CurricularOAEntity>>

    @Query("SELECT * FROM curricular_oas WHERE status = 'PUBLICADO'")
    fun getPublishedOAs(): Flow<List<CurricularOAEntity>>
}

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Query("SELECT * FROM questions WHERE oaCode = :oaCode")
    fun getQuestionsByOA(oaCode: String): Flow<List<QuestionEntity>>
}

@Dao
interface EvaluationConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: EvaluationConfigEntity)

    @Query("SELECT * FROM evaluation_configs WHERE oaCode = :oaCode LIMIT 1")
    fun getConfigByOA(oaCode: String): Flow<EvaluationConfigEntity?>

    @Query("SELECT * FROM evaluation_configs")
    fun getAllConfigs(): Flow<List<EvaluationConfigEntity>>
}

@Dao
interface AttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: StudentAttemptEntity)

    @Query("SELECT * FROM student_attempts WHERE studentUsername = :studentUsername AND oaCode = :oaCode")
    fun getAttemptsByStudentAndOA(studentUsername: String, oaCode: String): Flow<List<StudentAttemptEntity>>

    @Query("SELECT * FROM student_attempts")
    fun getAllAttempts(): Flow<List<StudentAttemptEntity>>
}

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReport(report: PedagogicalReportEntity)

    @Query("SELECT * FROM pedagogical_reports WHERE studentUsername = :studentUsername")
    fun getReportsForStudent(studentUsername: String): Flow<List<PedagogicalReportEntity>>
    @Query("SELECT * FROM pedagogical_reports")
    fun getAllReports(): Flow<List<PedagogicalReportEntity>>
}