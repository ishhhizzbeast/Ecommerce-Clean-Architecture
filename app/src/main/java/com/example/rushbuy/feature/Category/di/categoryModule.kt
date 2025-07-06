package com.example.rushbuy.feature.Category.di

import androidx.lifecycle.SavedStateHandle
import com.example.rushbuy.feature.Category.domain.usecase.GetProductsByCategoryUseCase
import com.example.rushbuy.feature.Category.presentation.viewmodel.CategoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val categoryModule = module{
    factory {
        GetProductsByCategoryUseCase(productRepository = get())
    }
    viewModel { (handle: SavedStateHandle) ->
        CategoryViewModel(
            savedStateHandle = handle,
            getProductsByCategoryUseCase = get(), // Koin will find an instance of GetProductsByCategoryUseCase
            productRepository = get() // Koin will find an instance of IProductRepository
        )
    }
}
