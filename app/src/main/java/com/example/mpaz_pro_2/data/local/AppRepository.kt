package com.example.mpaz_pro_2.data.local

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val curricularDao: CurricularDao,
    private val questionDao: QuestionDao,
    private val evaluationConfigDao: EvaluationConfigDao,
    private val attemptDao: AttemptDao,
    private val reportDao: ReportDao
) {
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allStudents: Flow<List<UserEntity>> = userDao.getStudents()
    val allOAs: Flow<List<CurricularOAEntity>> = curricularDao.getAllOAs()
    val authorizedOAs: Flow<List<CurricularOAEntity>> = curricularDao.getAuthorizedOAs()
    val publishedOAs: Flow<List<CurricularOAEntity>> = curricularDao.getPublishedOAs()
    val allAttempts: Flow<List<StudentAttemptEntity>> = attemptDao.getAllAttempts()
    val allReports: Flow<List<PedagogicalReportEntity>> = reportDao.getAllReports()

    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun getUserByUsername(username: String): UserEntity? = userDao.getUserByUsername(username)

    suspend fun updateOaStatus(oa: CurricularOAEntity) = curricularDao.updateOA(oa)
    suspend fun insertOA(oa: CurricularOAEntity) = curricularDao.insertOA(oa)
    suspend fun saveEvaluationConfig(config: EvaluationConfigEntity) = evaluationConfigDao.insertOrUpdateConfig(config)
    suspend fun saveStudentAttempt(attempt: StudentAttemptEntity) = attemptDao.insertAttempt(attempt)
    suspend fun saveReport(report: PedagogicalReportEntity) = reportDao.insertOrUpdateReport(report)
    suspend fun insertQuestion(question: QuestionEntity) = questionDao.insertQuestion(question)

    fun getQuestionsForOa(oaCode: String): Flow<List<QuestionEntity>> = questionDao.getQuestionsByOA(oaCode)
    fun getAttemptsForStudent(studentUsername: String, oaCode: String): Flow<List<StudentAttemptEntity>> = attemptDao.getAttemptsByStudentAndOA(studentUsername, oaCode)
    fun getConfigForOa(oaCode: String): Flow<EvaluationConfigEntity?> = evaluationConfigDao.getConfigByOA(oaCode)
    fun getReportsForStudent(username: String): Flow<List<PedagogicalReportEntity>> = reportDao.getReportsForStudent(username)
}