package com.example.rushbuy.feature.splash.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushbuy.feature.splash.domain.usecase.CheckNetworkUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val checkNetworkUseCase: CheckNetworkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<SplashNavigationEvent?>(null)
    val navigationEvent: StateFlow<SplashNavigationEvent?> = _navigationEvent.asStateFlow()

    fun startSplashSequence() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Wait for animation to complete
            delay(2000)

            val hasInternet = checkNetworkUseCase()
            _uiState.value = _uiState.value.copy(isLoading = false, hasInternet = hasInternet)

            if (hasInternet) {
                _navigationEvent.value = SplashNavigationEvent.NavigateToLogin
            } else {
                _navigationEvent.value = SplashNavigationEvent.NavigateToHome
            }
        }
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }
}

data class SplashUiState(
    val isLoading: Boolean = false,
    val hasInternet: Boolean = true
)

sealed class SplashNavigationEvent {
    object NavigateToLogin : SplashNavigationEvent()
    object NavigateToHome : SplashNavigationEvent()
}