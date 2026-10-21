package com.example.mpaz_pro_2.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateEvaluation(evaluation: EvaluationEntity)

    @Query("SELECT * FROM evaluations WHERE id = 1 LIMIT 1")
    fun getActiveEvaluation(): Flow<EvaluationEntity?>
}