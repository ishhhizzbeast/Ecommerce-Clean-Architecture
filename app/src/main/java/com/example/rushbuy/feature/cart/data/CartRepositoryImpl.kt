package com.example.rushbuy.feature.cart.data

import com.example.rushbuy.feature.cart.domain.model.CartItem
import com.example.rushbuy.feature.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartRepositoryImpl : CartRepository {

    // MutableStateFlow to hold the current list of cart items in memory.
    // It's private to ensure that updates only happen through the repository's methods.
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    // Expose the cart items as an immutable StateFlow for external observation.
    // This makes sure consumers (like Use Cases/ViewModel) only read and cannot directly modify.
    override fun getCartItems(): Flow<List<CartItem>> = _cartItems.asStateFlow()

    /**
     * Adds a product to the cart. If the product already exists, its quantity is updated.
     */
    override suspend fun addToCart(productId: String, name: String, imageUrl: String, price: Double, quantity: Int) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.productId == productId }
            if (existingItem != null) {
                // If item exists, increase its quantity
                currentItems.map { item ->
                    if (item.productId == productId) {
                        item.copy(quantity = item.quantity + quantity)
                    } else {
                        item
                    }
                }
            } else {
                // If item does not exist, add a new one to the list
                currentItems + CartItem(productId, name, imageUrl, price, quantity)
            }
        }
    }

    /**
     * Updates the quantity of a specific product in the cart.
     * If newQuantity is 0 or less, the item is removed.
     */
    override suspend fun updateCartItemQuantity(productId: String, newQuantity: Int) {
        _cartItems.update { currentItems ->
            val updatedList = currentItems.toMutableList() // Create a mutable copy to modify
            val index = updatedList.indexOfFirst { it.productId == productId }

            if (index != -1) { // Check if the item exists
                if (newQuantity > 0) {
                    // Update quantity if positive
                    updatedList[index] = updatedList[index].copy(quantity = newQuantity)
                } else {
                    // Remove item if quantity is zero or less
                    updatedList.removeAt(index)
                }
            }
            updatedList // Return the modified list
        }
    }

    /**
     * Removes a specific product from the cart.
     */
    override suspend fun removeFromCart(productId: String) {
        _cartItems.update { currentItems ->
            currentItems.filter { it.productId != productId } // Filter out the item to be removed
        }
    }

    /**
     * Clears all items from the cart.
     */
    override suspend fun clearCart() {
        _cartItems.value = emptyList() // Set the cart to an empty list
    }
}