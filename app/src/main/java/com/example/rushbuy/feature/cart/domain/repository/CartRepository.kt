package com.example.rushbuy.feature.cart.domain.repository

import com.example.rushbuy.feature.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    /**
     * Retrieves the current list of items in the cart as a Flow.
     * Changes to the cart will be emitted via this Flow.
     */
    fun getCartItems(): Flow<List<CartItem>>

    /**
     * Adds a product to the cart. If the product already exists, its quantity is updated.
     */
    suspend fun addToCart(productId: String, name: String, imageUrl: String, price: Double, quantity: Int = 1)

    /**
     * Updates the quantity of a specific product in the cart.
     * If newQuantity is 0 or less, the item should be removed.
     */
    suspend fun updateCartItemQuantity(productId: String, newQuantity: Int)

    /**
     * Removes a specific product from the cart.
     */
    suspend fun removeFromCart(productId: String)

    /**
     * Clears all items from the cart.
     */
    suspend fun clearCart()
}