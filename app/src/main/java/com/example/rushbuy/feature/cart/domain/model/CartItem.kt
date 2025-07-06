package com.example.rushbuy.feature.cart.domain.model

data class CartItem(
    val productId: String, // Unique ID of the product
    val name: String,      // Name of the product
    val imageUrl: String,  // Image URL for display
    val price: Double,     // Price per unit of the product
    var quantity: Int      // Quantity of this product in the cart
) {
    // A computed property to easily get the total price for this specific item
    val totalPrice: Double
        get() = price * quantity
}