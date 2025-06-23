package com.example.rushbuy.feature.auth.domain.repository

import com.example.rushbuy.feature.auth.domain.model.AuthResult
import com.example.rushbuy.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun loginWithEmail(email: String,password:String): AuthResult
    suspend fun signInWithGoogleCredential(idToken: String): AuthResult
    suspend fun register(username: String, email: String, password: String, address: String?): AuthResult
    suspend fun logout(): AuthResult
    suspend fun getCurrentUser(): User?
}