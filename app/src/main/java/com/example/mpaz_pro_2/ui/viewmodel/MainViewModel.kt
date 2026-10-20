package com.example.mpaz_pro_2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaz_pro_2.data.local.AppRepository
import com.example.mpaz_pro_2.UserEntity
import com.example.mpaz_pro_2.model.ValidationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(private val repository: AppRepository? = null) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

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

    // Guardar usuario en Room usando Corrutinas
    fun registerUser(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.nameError == null && currentState.name.isNotBlank()) {
            viewModelScope.launch {
                val newUser = UserEntity(
                    id = UUID.randomUUID().toString(),
                    name = currentState.name,
                    role = currentState.role
                )
                repository?.insertUser(newUser)
                onSuccess()
            }
        }
    }
}