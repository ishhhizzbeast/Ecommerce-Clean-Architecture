package com.example.rushbuy.feature.profile.domain.usecase

import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.feature.profile.domain.repository.UserProfileRepository
import com.example.rushbuy.feature.profile.domain.model.UserProfile


class GetUserProfileUseCase(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(): ResultState<UserProfile> {
        return repository.getUserProfile()
    }
}

class LogoutUseCase(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(): ResultState<Unit> {
        return repository.logout()
    }
}