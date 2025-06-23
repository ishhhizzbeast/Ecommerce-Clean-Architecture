package com.example.rushbuy.feature.auth.presentation.login.state


data class GoogleLoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)