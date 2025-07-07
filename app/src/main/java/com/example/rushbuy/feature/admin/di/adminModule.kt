package com.example.rushbuy.feature.admin.di

import com.example.rushbuy.feature.admin.presentation.viewmodel.AddEditProductViewModel
import com.example.rushbuy.feature.admin.domain.AddProductUseCase
import com.example.rushbuy.feature.admin.domain.DeleteProductUseCase
import com.example.rushbuy.feature.admin.domain.GetAdminProductsUseCase
import com.example.rushbuy.feature.admin.domain.UpdateProductUseCase
import com.example.rushbuy.feature.admin.presentation.viewmodel.AdminHomeViewModel
import org.koin.dsl.module

val adminModule = module {
    // --- Use Cases ---
    // These use cases depend on IProductRepository, which *must* be provided by another module (e.g., dataModule).
    // Koin will automatically resolve IProductRepository via 'get()' if it's available in any loaded module.

    single { GetAdminProductsUseCase(productRepository = get()) } // 'get()' will resolve IProductRepository
    single { AddProductUseCase(productRepository = get()) }
    single { UpdateProductUseCase(productRepository = get()) }
    single { DeleteProductUseCase(productRepository = get()) }

    // --- ViewModels ---

    // Provides AdminHomeViewModel
    // All dependencies (UseCases) are 'single'tons defined above, so Koin resolves them easily.
    factory {
        AdminHomeViewModel(
            getAdminProductsUseCase = get(),
            deleteProductUseCase = get()
        )
    }

    // Provides AddEditProductViewModel
    // It requires SavedStateHandle from Koin's parameter scope when created by koinViewModel().
    factory { parameters ->
        AddEditProductViewModel(
            productRepository = get(), // Resolves IProductRepository from the loaded modules
            addProductUseCase = get(),
            updateProductUseCase = get(),
            savedStateHandle = parameters.get(),
            triggerNewItemNotificationUseCase = get()
        )
    }
}