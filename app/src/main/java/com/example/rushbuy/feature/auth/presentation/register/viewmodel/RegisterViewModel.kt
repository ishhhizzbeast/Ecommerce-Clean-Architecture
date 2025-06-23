package com.example.rushbuy.feature.auth.presentation.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushbuy.feature.auth.domain.model.AuthResult
import com.example.rushbuy.feature.auth.domain.usecase.RegisterUseCase
import com.example.rushbuy.feature.auth.presentation.register.state.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    fun updateAddress(address: String) {
        _uiState.value = _uiState.value.copy(address = address)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    fun register() {
        viewModelScope.launch {
            // Ensure state is reset before starting a new registration attempt
            // This is crucial for LaunchedEffect to re-trigger on subsequent successes
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isRegistrationSuccessful = false // Reset this flag at the start of new attempt
            )

            val result = registerUseCase(
                username = _uiState.value.username,
                email = _uiState.value.email,
                password = _uiState.value.password,
                confirmPassword = _uiState.value.confirmPassword,
                address = _uiState.value.address
            )

             when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistrationSuccessful = true, // Set to true on success
                        errorMessage = null
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistrationSuccessful = false,
                        errorMessage = result.message
                    )
                }
                // REMOVED: AuthResult.Loading branch.
                // For a suspend function that directly returns a final result (Success/Error),
                // AuthResult.Loading is not expected here. Managing isLoading from the ViewModel
                // directly is the correct pattern for this use case. If this branch was hit,
                // it would prevent isLoading from becoming false and success from triggering.
                AuthResult.Loading -> {_uiState.value = _uiState.value.copy(isLoading = true)}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetRegistrationState() {
        _uiState.value = RegisterUiState()
    }
}