package com.example.rushbuy.feature.auth.domain.usecase

import com.example.rushbuy.feature.auth.domain.model.AuthResult
import com.example.rushbuy.feature.auth.domain.model.User
import com.example.rushbuy.feature.auth.domain.repository.AuthRepository


class LoginWithEmailUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Email and password cannot be empty")
        }

        return repository.loginWithEmail(email, password)
    }
}


class LoginWithGoogleUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthResult {
        return repository.signInWithGoogleCredential(idToken)
    }
}


class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        address: String
    ): AuthResult {
        // Basic validation
        when {
            username.isBlank() -> return AuthResult.Error("Username is required")
            email.isBlank() -> return AuthResult.Error("Email is required")
            password.isBlank() -> return AuthResult.Error("Password is required")
            address.isBlank() -> return AuthResult.Error("Address is required")
            password != confirmPassword -> return AuthResult.Error("Passwords do not match")
            password.length < 6 -> return AuthResult.Error("Password must be at least 6 characters")
        }

        return repository.register(username,email,password,address)
    }
}


class LogoutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthResult {
        return repository.logout()
    }
}


class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}