package com.example.rushbuy.feature.cart.domain.usecase

import com.example.rushbuy.feature.cart.domain.model.CartItem
import com.example.rushbuy.feature.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class AddToCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(productId: String, name: String, imageUrl: String, price: Double, quantity: Int = 1) {
        repository.addToCart(productId, name, imageUrl, price, quantity)
    }
}

class GetCartItemsUseCase(private val repository: CartRepository) {
    operator fun invoke(): Flow<List<CartItem>> {
        return repository.getCartItems()
    }
}

class UpdateCartItemQuantityUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(productId: String, newQuantity: Int) {
        repository.updateCartItemQuantity(productId, newQuantity)
    }
}


class RemoveFromCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(productId: String) {
        repository.removeFromCart(productId)
    }
}

class ClearCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke() {
        repository.clearCart()
    }
}