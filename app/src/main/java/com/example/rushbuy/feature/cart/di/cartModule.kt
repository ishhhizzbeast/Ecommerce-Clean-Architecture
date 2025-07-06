package com.example.rushbuy.feature.cart.di


import com.example.rushbuy.feature.cart.data.CartRepositoryImpl
import com.example.rushbuy.feature.cart.domain.repository.CartRepository
import com.example.rushbuy.feature.cart.domain.usecase.AddToCartUseCase
import com.example.rushbuy.feature.cart.domain.usecase.ClearCartUseCase
import com.example.rushbuy.feature.cart.domain.usecase.GetCartItemsUseCase
import com.example.rushbuy.feature.cart.domain.usecase.RemoveFromCartUseCase
import com.example.rushbuy.feature.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.rushbuy.feature.cart.presentation.viewmodel.CartViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val cartModule = module {


    // Repository
    single<CartRepository> { CartRepositoryImpl() }

    // Use Cases
    factory { GetCartItemsUseCase(get()) }
    factory { AddToCartUseCase(get()) }
    factory { UpdateCartItemQuantityUseCase(get()) }
    factory { RemoveFromCartUseCase(get()) }
    factory { ClearCartUseCase(get()) }

    // ViewModel
    viewModel {
        CartViewModel(
            getCartItemsUseCase = get(),
            addToCartUseCase = get(),
            updateCartItemQuantityUseCase = get(),
            removeFromCartUseCase = get(),
            clearCartUseCase = get(),
        )
    }
}