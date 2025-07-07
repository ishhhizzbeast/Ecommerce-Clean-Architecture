package com.example.rushbuy.feature.profile.di

import com.example.rushbuy.feature.profile.data.UserProfileRepositoryImpl
import com.example.rushbuy.feature.profile.domain.repository.UserProfileRepository
import com.example.rushbuy.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.rushbuy.feature.profile.presentation.viewmodel.UserProfileViewModel
import com.example.rushbuy.feature.profile.domain.usecase.LogoutUseCase
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val userProfileModule = module {

    // Provide FirebaseAuth instance
    // Make sure FirebaseAuth is initialized elsewhere (e.g., in MyApplication)
    // If you already provide FirebaseAuth as a singleton in a common module (e.g., core/di),
    // you don't need to redeclare it here. Just make sure it's available.
    single { FirebaseAuth.getInstance() }

    // Provide UserProfileRepository implementation
    single<UserProfileRepository> {
        UserProfileRepositoryImpl(firebaseAuth = get()) // 'get()' resolves FirebaseAuth
    }

    // Provide GetUserProfileUseCase
    factory {
        GetUserProfileUseCase(repository = get()) // 'get()' resolves UserProfileRepository
    }

    // Provide LogoutUseCase
    factory {
        LogoutUseCase(repository = get()) // 'get()' resolves UserProfileRepository
    }

    // Provide UserProfileViewModel
    viewModel {
        UserProfileViewModel(
            getUserProfileUseCase = get(), // 'get()' resolves GetUserProfileUseCase
            logoutUseCase = get()          // 'get()' resolves LogoutUseCase
        )
    }
}