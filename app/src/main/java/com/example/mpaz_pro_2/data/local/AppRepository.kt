package com.example.mpaz_pro_2.data.local

import com.example.mpaz_pro_2.data.model.ObjetivoDto
import com.example.mpaz_pro_2.data.model.Post
import com.example.mpaz_pro_2.data.remote.ApiService
import com.example.mpaz_pro_2.data.remote.BackendApiService
import com.example.mpaz_pro_2.data.remote.BackendRetrofitInstance
import com.example.mpaz_pro_2.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val curricularDao: CurricularDao,
    private val questionDao: QuestionDao,
    private val evaluationConfigDao: EvaluationConfigDao,
    private val attemptDao: AttemptDao,
    private val reportDao: ReportDao,
    private val apiService: ApiService = RetrofitInstance.api,
    private val backendApi: BackendApiService = BackendRetrofitInstance.api
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

    // --- API Externa ---
    suspend fun getExternalPosts(): List<Post> = apiService.getPosts()

    // --- Microservicio Spring Boot (CRUD) ---
    suspend fun getBackendObjetivos(): List<ObjetivoDto> = backendApi.getObjetivos()
    suspend fun createBackendObjetivo(oa: ObjetivoDto): ObjetivoDto = backendApi.createObjetivo(oa)
    suspend fun updateBackendObjetivo(id: Long, oa: ObjetivoDto): ObjetivoDto = backendApi.updateObjetivo(id, oa)
    suspend fun deleteBackendObjetivo(id: Long): Boolean = backendApi.deleteObjetivo(id).isSuccessful
}