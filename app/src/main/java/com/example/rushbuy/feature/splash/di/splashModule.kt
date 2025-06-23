package com.example.rushbuy.feature.splash.di

import com.example.rushbuy.feature.splash.data.repository.NetworkRepositoryImpl
import com.example.rushbuy.feature.splash.domain.repository.NetworkRepository
import com.example.rushbuy.feature.splash.domain.usecase.CheckNetworkUseCase
import com.example.rushbuy.feature.splash.presentation.viewmodel.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val splashModule = module{
    // Repository
    single<NetworkRepository> {
        NetworkRepositoryImpl(androidContext())
    }
    // Use Cases
    single {
        CheckNetworkUseCase(get())
    }
    viewModelOf(::SplashViewModel)
}