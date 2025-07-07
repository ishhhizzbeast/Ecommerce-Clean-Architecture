package com.example.rushbuy.feature.profile.domain.repository

import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.feature.profile.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun getUserProfile(): ResultState<UserProfile>
    suspend fun logout(): ResultState<Unit>
}