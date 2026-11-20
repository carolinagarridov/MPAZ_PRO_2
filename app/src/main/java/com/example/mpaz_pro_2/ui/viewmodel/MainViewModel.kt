package com.example.mpaz_pro_2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaz_pro_2.data.local.*
import com.example.mpaz_pro_2.data.model.ObjetivoDto
import com.example.mpaz_pro_2.data.model.Post
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    // Objetivos de Aprendizaje (OAs - Room)
    val allOAs: StateFlow<List<CurricularOAEntity>> = repository.allOAs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authorizedOAs: StateFlow<List<CurricularOAEntity>> = repository.authorizedOAs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedOAs: StateFlow<List<CurricularOAEntity>> = repository.publishedOAs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estudiantes e Informes Pedagógicos (Room)
    val allStudents: StateFlow<List<UserEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<PedagogicalReportEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttempts: StateFlow<List<StudentAttemptEntity>> = repository.allAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ----------------------------------------------------
    // Estados Remotos (API Externa + Microservicio Spring Boot)
    // ----------------------------------------------------

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList.asStateFlow()

    private val _backendObjetivos = MutableStateFlow<List<ObjetivoDto>>(emptyList())
    val backendObjetivos: StateFlow<List<ObjetivoDto>> = _backendObjetivos.asStateFlow()

    private val _backendStatusMsg = MutableStateFlow<String?>(null)
    val backendStatusMsg: StateFlow<String?> = _backendStatusMsg.asStateFlow()

    init {
        fetchPosts()
        loadBackendObjetivos()
    }

    // ----------------------------------------------------
    // Consultas Específicas (Room)
    // ----------------------------------------------------

    fun getQuestionsForOa(oaCode: String): Flow<List<QuestionEntity>> {
        return repository.getQuestionsForOa(oaCode)
    }

    fun getConfigForOa(oaCode: String): Flow<EvaluationConfigEntity?> {
        return repository.getConfigForOa(oaCode)
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        return repository.getUserByUsername(username)
    }

    fun getReportsForStudent(studentUsername: String): Flow<List<PedagogicalReportEntity>> {
        return repository.getReportsForStudent(studentUsername)
    }

    fun getAttemptsForStudent(studentUsername: String, oaCode: String): Flow<List<StudentAttemptEntity>> {
        return repository.getAttemptsForStudent(studentUsername, oaCode)
    }

    // ----------------------------------------------------
    // Guardado y Actualización (Room)
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

    // ----------------------------------------------------
    // Operaciones de Red (API REST Externa y CRUD Spring Boot)
    // ----------------------------------------------------

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                _postList.value = repository.getExternalPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadBackendObjetivos() {
        viewModelScope.launch {
            try {
                _backendObjetivos.value = repository.getBackendObjetivos()
            } catch (e: Exception) {
                _backendStatusMsg.value = "Sin conexión a Spring Boot (puerto 8080)"
            }
        }
    }

    fun addBackendObjetivo(subject: String, code: String, title: String, desc: String) {
        viewModelScope.launch {
            try {
                val nuevo = ObjetivoDto(subject = subject, oaCode = code, oaTitle = title, description = desc)
                repository.createBackendObjetivo(nuevo)
                _backendStatusMsg.value = "OA creado en Spring Boot"
                loadBackendObjetivos()
            } catch (e: Exception) {
                _backendStatusMsg.value = "Error al crear: ${e.message}"
            }
        }
    }

    fun toggleStatusBackendObjetivo(oa: ObjetivoDto) {
        val id = oa.id ?: return
        viewModelScope.launch {
            try {
                val nuevoEstado = if (oa.status == "PUBLICADO") "AUTORIZADO" else "PUBLICADO"
                repository.updateBackendObjetivo(id, oa.copy(status = nuevoEstado))
                _backendStatusMsg.value = "Actualizado a $nuevoEstado"
                loadBackendObjetivos()
            } catch (e: Exception) {
                _backendStatusMsg.value = "Error al actualizar: ${e.message}"
            }
        }
    }

    fun removeBackendObjetivo(id: Long) {
        viewModelScope.launch {
            try {
                if (repository.deleteBackendObjetivo(id)) {
                    _backendStatusMsg.value = "OA eliminado del servidor"
                    loadBackendObjetivos()
                }
            } catch (e: Exception) {
                _backendStatusMsg.value = "Error al eliminar: ${e.message}"
            }
        }
    }
}