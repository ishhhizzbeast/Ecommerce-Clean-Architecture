package com.example.rushbuy.feature.profile.domain.model

data class UserProfile(
    val uid: String,        // Unique ID from Firebase
    val displayName: String?,
    val email: String?,
)