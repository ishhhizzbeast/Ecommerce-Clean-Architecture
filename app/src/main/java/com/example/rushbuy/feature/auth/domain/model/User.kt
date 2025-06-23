package com.example.rushbuy.feature.auth.domain.model

data class User (
    val id: String,
    val name: String,
    val email: String,
    val address: String? = null,
)
enum class UserRole{
    USER,
    ADMIN
}