package com.example.rushbuy.feature.auth.domain.model

// domain/model/AuthResult.kt
sealed class AuthResult {
    data class Success(val user: User, val message: String = "") : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}