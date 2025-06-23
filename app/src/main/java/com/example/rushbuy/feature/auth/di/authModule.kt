package com.example.rushbuy.feature.auth.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rushbuy.feature.auth.data.AuthRepositoryImpl
import com.example.rushbuy.feature.auth.domain.repository.AuthRepository
import com.example.rushbuy.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.example.rushbuy.feature.auth.domain.usecase.LoginWithEmailUseCase
import com.example.rushbuy.feature.auth.domain.usecase.LoginWithGoogleUseCase
import com.example.rushbuy.feature.auth.domain.usecase.LogoutUseCase
import com.example.rushbuy.feature.auth.domain.usecase.RegisterUseCase
import com.example.rushbuy.feature.auth.presentation.login.viewmodel.EmailLoginViewModel
import com.example.rushbuy.feature.auth.presentation.login.viewmodel.GoogleLoginViewModel
import com.example.rushbuy.feature.auth.presentation.register.viewmodel.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {

    // Firebase instances
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Repository
    single<AuthRepository> {
        AuthRepositoryImpl(
            firebaseAuth = get(),
            firestore = get()
        )
    }

    // Use Cases
    single { RegisterUseCase(repository = get()) }
    single { LoginWithEmailUseCase(repository = get()) }
    single { LoginWithGoogleUseCase(repository = get()) }
    single { LogoutUseCase(repository = get()) }
    single { GetCurrentUserUseCase(repository = get()) }

    // ViewModels
    factory {
        EmailLoginViewModel(
            loginWithEmailUseCase = get()
        )
    }

    factory {
        GoogleLoginViewModel(
            loginWithGoogleUseCase = get(),
            authRepository = get()
        )
    }

    factory {
        RegisterViewModel(
            registerUseCase = get()
        )
    }
}