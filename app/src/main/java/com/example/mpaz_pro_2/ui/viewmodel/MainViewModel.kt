package com.example.mpaz_pro_2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaz_pro_2.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    // Objetivos de Aprendizaje (OAs)
    val allOAs: StateFlow<List<CurricularOAEntity>> = repository.allOAs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authorizedOAs: StateFlow<List<CurricularOAEntity>> = repository.authorizedOAs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedOAs: StateFlow<List<CurricularOAEntity>> = repository.publishedOAs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estudiantes e Informes Pedagógicos
    val allStudents: StateFlow<List<UserEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<PedagogicalReportEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttempts: StateFlow<List<StudentAttemptEntity>> = repository.allAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ----------------------------------------------------
    // Consultas Específicas
    // ----------------------------------------------------

    // Obtener preguntas registradas para un OA específico
    fun getQuestionsForOa(oaCode: String): Flow<List<QuestionEntity>> {
        return repository.getQuestionsForOa(oaCode)
    }

    // Obtener parámetros de configuración de la evaluación para un OA
    fun getConfigForOa(oaCode: String): Flow<EvaluationConfigEntity?> {
        return repository.getConfigForOa(oaCode)
    }

    // Obtener usuario por nombre de usuario (para fotos de perfil y roles)
    suspend fun getUserByUsername(username: String): UserEntity? {
        return repository.getUserByUsername(username)
    }

    // Consultar informes de un estudiante específico
    fun getReportsForStudent(studentUsername: String): Flow<List<PedagogicalReportEntity>> {
        return repository.getReportsForStudent(studentUsername)
    }

    // Consultar intentos de un estudiante para un OA
    fun getAttemptsForStudent(studentUsername: String, oaCode: String): Flow<List<StudentAttemptEntity>> {
        return repository.getAttemptsForStudent(studentUsername, oaCode)
    }

    // ----------------------------------------------------
    // Guardado y Actualización
    // ----------------------------------------------------

    fun updateOaStatus(oa: CurricularOAEntity) {
        viewModelScope.launch { repository.updateOaStatus(oa) }
    }

    fun createOa(oa: CurricularOAEntity) {
        viewModelScope.launch { repository.insertOA(oa) }
    }

    fun saveQuestion(question: QuestionEntity) {
        viewModelScope.launch { repository.insertQuestion(question) }
    }

    fun saveEvaluationConfig(config: EvaluationConfigEntity) {
        viewModelScope.launch { repository.saveEvaluationConfig(config) }
    }

    fun saveStudentAttempt(attempt: StudentAttemptEntity) {
        viewModelScope.launch { repository.saveStudentAttempt(attempt) }
    }

    fun saveReport(report: PedagogicalReportEntity) {
        viewModelScope.launch { repository.saveReport(report) }
    }
}