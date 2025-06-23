package com.example.rushbuy.feature.splash.domain.repository

interface NetworkRepository {
    suspend fun isNetworkAvailable(): Boolean
}