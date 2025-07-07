package com.example.rushbuy.core.foundation.data.local.source

import com.example.rushbuy.core.foundation.domain.model.Product
import kotlinx.coroutines.flow.Flow


interface IProductLocalDataSource {
    fun getAllProducts(): Flow<List<Product>> // Returns domain models
    suspend fun getProductById(productId: Int): Product?// Returns domain model
    suspend fun insertProduct(product: Product):Long
    suspend fun insertProducts(products: List<Product>) // Accepts list of domain models
    suspend fun clearAllProducts() // Useful for clearing cache
    fun searchProducts(query: String): Flow<List<Product>>
    suspend fun deleteProductById(productId: Int) // Added for direct delete by ID
    suspend fun getProductCount(): Int // Added for PagingSource logic (to check if local cache is empty)
    suspend fun getAllCategories(): Flow<List<String>>
}