package com.example.mpaz_pro_2.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaz_pro_2.data.local.AppDatabase
import com.example.mpaz_pro_2.data.local.AppRepository
import com.example.mpaz_pro_2.data.local.EvaluationEntity
import com.example.mpaz_pro_2.data.local.UserEntity
import com.example.mpaz_pro_2.model.ValidationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _activeEvalState = MutableStateFlow<EvaluationEntity?>(null)
    val activeEvaluation: StateFlow<EvaluationEntity?> = _activeEvalState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(db.userDao(), db.evaluationDao())

        viewModelScope.launch {
            repository.activeEvaluation.collect { eval ->
                _activeEvalState.value = eval
            }
        }
    }

    fun onNameChanged(newName: String) {
        val error = ValidationEngine.validateUserName(newName)
        _uiState.update { it.copy(name = newName, nameError = error) }
    }

    fun onRoleChanged(newRole: String) {
        _uiState.update { it.copy(role = newRole) }
    }

    fun onImageSelected(uri: String?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun registerUser(onSuccess: () -> Unit) {
        val nameError = ValidationEngine.validateUserName(_uiState.value.name)
        if (nameError == null && _uiState.value.name.isNotBlank()) {
            viewModelScope.launch {
                val user = com.example.mpaz_pro_2.data.local.UserEntity(
                    name = _uiState.value.name,
                    role = _uiState.value.role,
                    profileImageUri = _uiState.value.selectedImageUri
                )
                repository.insertUser(user)
                onSuccess()
            }
        } else {
            _uiState.update { it.copy(nameError = nameError ?: "El nombre es obligatorio") }
        }
    }

    fun saveEvaluation(oaTitle: String, maxAttempts: Int, evalDate: String, isGraded: Boolean, isEnabled: Boolean, onSaved: () -> Unit) {
        viewModelScope.launch {
            val eval = EvaluationEntity(
                id = 1,
                oaTitle = oaTitle,
                maxAttempts = maxAttempts,
                evalDate = evalDate,
                isGraded = isGraded,
                isEnabled = isEnabled
            )
            repository.saveEvaluation(eval)
            onSaved()
        }
    }
}