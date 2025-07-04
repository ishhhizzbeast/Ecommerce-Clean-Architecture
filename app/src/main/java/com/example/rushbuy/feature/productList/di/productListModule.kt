package com.example.rushbuy.feature.productList.di

import com.example.rushbuy.feature.productList.domain.usecase.GetProductsUseCase
import com.example.rushbuy.feature.productList.domain.usecase.SearchProductsUseCase
import com.example.rushbuy.feature.productList.presentation.viewmodel.ProductDetailViewModel
import com.example.rushbuy.feature.productList.presentation.viewmodel.ProductListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val productListModule = module{
    // --- Use Cases ---
    // GetProductsUseCase depends on IProductRepository
    factory { GetProductsUseCase(repository = get()) }

    // SearchProductsUseCase depends on IProductRepository
    factory { SearchProductsUseCase(repository = get()) }

    // --- ViewModels ---
    // ProductListViewModel depends on GetProductsUseCase and SearchProductsUseCase
    viewModel {
        ProductListViewModel(
            getProductsUseCase = get(), // Koin will resolve GetProductsUseCase
            searchProductsUseCase = get() // Koin will resolve SearchProductsUseCase
        )

    }
    viewModel {
        ProductDetailViewModel(get())
    }
}