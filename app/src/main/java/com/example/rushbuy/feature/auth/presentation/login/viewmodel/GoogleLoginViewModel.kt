package com.example.rushbuy.feature.auth.presentation.login.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushbuy.feature.auth.domain.model.AuthResult
import com.example.rushbuy.feature.auth.domain.repository.AuthRepository
import com.example.rushbuy.feature.auth.domain.usecase.LoginWithGoogleUseCase
import com.example.rushbuy.feature.auth.presentation.login.state.GoogleLoginUiState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.common.collect.TreeTraverser.using
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoogleLoginViewModel(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "GoogleLoginViewModel"
        // Add your web client ID here
        private const val webClientId = "368356765150-gnana91nrtih2r1jplv5j88tqnjeasrd.apps.googleusercontent.com"
    }

    private val _uiState = MutableStateFlow(GoogleLoginUiState())
    val uiState: StateFlow<GoogleLoginUiState> = _uiState.asStateFlow()

//    fun selectRole(role: UserRole) {
//        _uiState.value = _uiState.value.copy(selectedRole = role, errorMessage = null)
//    }

    // Main Google Sign-In function that handles the complete flow
    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    isLoginSuccessful = false
                )
                //Google Sign-In request using Credential Manager
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                handleGoogleSignInResult(result)

            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In failed", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Google Sign-In failed: ${e.message}",
                    isLoginSuccessful = false
                )
            }
        }
    }

    private fun handleGoogleSignInResult(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is GoogleIdTokenCredential -> {
                try {
                    val idToken = credential.idToken
                    Log.d(TAG, "Google Sign-In successful, token received: ${idToken.take(10)}...")
                    signInWithGoogleToken(idToken)
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e(TAG, "Invalid Google ID token", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Invalid Google ID token"
                    )
                }
            }
            else -> {
                Log.e(TAG, "Unexpected credential type")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Unexpected credential type received"
                )
            }
        }
    }

    private fun signInWithGoogleToken(idToken: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                when (val result = loginWithGoogleUseCase(idToken)) {
                    is AuthResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                            errorMessage = null
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            isLoginSuccessful = false
                        )
                    }
                    is AuthResult.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during sign-in process", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Unexpected error occurred: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    fun resetLoginState() {
        _uiState.value = GoogleLoginUiState()
    }
}