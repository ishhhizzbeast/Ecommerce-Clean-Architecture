package com.example.rushbuy.feature.splash.domain.usecase

import com.example.rushbuy.feature.splash.domain.repository.NetworkRepository

class CheckNetworkUseCase (
    private val networkRepository: NetworkRepository
    ) {
        suspend operator fun invoke(): Boolean {
            return networkRepository.isNetworkAvailable()
        }
}