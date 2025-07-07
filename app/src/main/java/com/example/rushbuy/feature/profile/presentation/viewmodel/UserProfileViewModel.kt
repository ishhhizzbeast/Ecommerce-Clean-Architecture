package com.example.rushbuy.feature.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.feature.profile.domain.model.UserProfile
import com.example.rushbuy.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.rushbuy.feature.profile.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserProfileState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null, // <--- ADDED THIS BACK!
    val error: String? = null,
    val isLoggedOut: Boolean = false // State to track successful logout
)

class UserProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileState(isLoading = true)) // Initial state with loading
    val uiState: StateFlow<UserProfileState> = _uiState.asStateFlow()

    init {
        // Fetch user profile as soon as the ViewModel is created
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null) // Set loading, clear previous error
            when (val result = getUserProfileUseCase()) {
                is ResultState.Success -> {
                    _uiState.value = _uiState.value.copy(
                        userProfile = result.data, // <--- IMPORTANT: SET THE USER PROFILE DATA HERE
                        isLoading = false,
                        error = null
                    )
                }
                is ResultState.Error -> {
                    _uiState.value = _uiState.value.copy(
                        userProfile = null, // Clear profile on error or if user is not found
                        isLoading = false,
                        error = result.message
                    )
                }
                is ResultState.Loading -> {
                    // This state from the use case might not be strictly necessary here
                    // as we set isLoading = true manually before the call.
                    // But if your repository emits Loading, you'd handle it here.
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
                is ResultState.Idle -> {
                    // Do nothing for Idle state, typically it's an initial state or after an operation completes.
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null) // Show loading for logout
            when (val result = logoutUseCase()) {
                is ResultState.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedOut = true, // Signal successful logout
                        userProfile = null, // <--- Clear user data after logout
                        error = null
                    )
                }
                is ResultState.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isLoggedOut = false // Logout failed
                    )
                }
                is ResultState.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
                is ResultState.Idle -> {
                    // Do nothing
                }
            }
        }
    }

    /**
     * Call this when navigating away from the profile screen after logout
     * or when the logout state has been consumed by the UI.
     */
    fun logoutHandled() {
        _uiState.value = _uiState.value.copy(isLoggedOut = false)
    }
}