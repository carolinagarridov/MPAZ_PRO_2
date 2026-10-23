package com.example.mpaz_pro_2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mpaz_pro_2.data.local.AppRepository
import com.example.mpaz_pro_2.data.local.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoginTab: Boolean = true,
    val usernameInput: String = "",
    val nameInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val roleInput: String = "ESTUDIANTE",
    val imageUriInput: String? = null,
    val usernameError: String? = null,
    val nameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false
)

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onTabChanged(isLogin: Boolean) {
        _uiState.update {
            it.copy(
                isLoginTab = isLogin,
                generalError = null,
                usernameError = null,
                passwordError = null,
                nameError = null,
                confirmPasswordError = null
            )
        }
    }

    fun onUsernameChanged(value: String) {
        _uiState.update { it.copy(usernameInput = value, usernameError = null, generalError = null) }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(nameInput = value, nameError = null, generalError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(passwordInput = value, passwordError = null, generalError = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPasswordInput = value, confirmPasswordError = null, generalError = null) }
    }

    fun onRoleChanged(role: String) {
        _uiState.update { it.copy(roleInput = role) }
    }

    fun onImageSelected(uriPath: String?) {
        _uiState.update { it.copy(imageUriInput = uriPath) }
    }

    fun reset() {
        _uiState.value = AuthUiState()
    }

    fun login(onSuccess: (UserEntity) -> Unit) {
        val state = _uiState.value
        if (state.usernameInput.isBlank()) {
            _uiState.update { it.copy(usernameError = "Ingrese un nombre de usuario.") }
            return
        }
        if (state.passwordInput.isBlank()) {
            _uiState.update { it.copy(passwordError = "Ingrese su contraseña.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, generalError = null) }

        viewModelScope.launch {
            val user = repository.getUserByUsername(state.usernameInput.trim())
            if (user != null && user.password == state.passwordInput) {
                _uiState.update { it.copy(isLoading = false) }
                onSuccess(user)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalError = "Usuario o contraseña incorrectos."
                    )
                }
            }
        }
    }

    fun register(onSuccess: (UserEntity) -> Unit) {
        val state = _uiState.value
        var hasError = false

        if (state.usernameInput.isBlank()) {
            _uiState.update { it.copy(usernameError = "El usuario no puede estar vacío.") }
            hasError = true
        }
        if (state.nameInput.isBlank()) {
            _uiState.update { it.copy(nameError = "El nombre completo es requerido.") }
            hasError = true
        }
        if (state.passwordInput.length < 4) {
            _uiState.update { it.copy(passwordError = "La contraseña debe tener al menos 4 caracteres.") }
            hasError = true
        }
        if (state.passwordInput != state.confirmPasswordInput) {
            _uiState.update { it.copy(confirmPasswordError = "Las contraseñas no coinciden.") }
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isLoading = true, generalError = null) }

        viewModelScope.launch {
            val existing = repository.getUserByUsername(state.usernameInput.trim())
            if (existing != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalError = "El nombre de usuario ya se encuentra registrado."
                    )
                }
            } else {
                val newUser = UserEntity(
                    username = state.usernameInput.trim(),
                    name = state.nameInput.trim(),
                    password = state.passwordInput,
                    role = state.roleInput,
                    profileImageUri = state.imageUriInput
                )
                repository.insertUser(newUser)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess(newUser)
            }
        }
    }
}

class AuthViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}