package com.example.mpaz_pro_2.data.local

import kotlinx.coroutines.flow.Flow


class AppRepository(
    private val userDao: UserDao,
    private val evaluationDao: EvaluationDao
) {
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val activeEvaluation: Flow<EvaluationEntity?> = evaluationDao.getActiveEvaluation()

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun saveEvaluation(evaluation: EvaluationEntity) {
        evaluationDao.insertOrUpdateEvaluation(evaluation)
    }
}