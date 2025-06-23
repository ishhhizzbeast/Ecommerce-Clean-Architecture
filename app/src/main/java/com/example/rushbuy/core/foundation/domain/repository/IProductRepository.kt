package com.example.rushbuy.core.foundation.domain.repository

import com.example.rushbuy.core.foundation.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    fun getProducts(): Flow<List<Product>> // Using Flow for reactive data streams
    suspend fun getProductById(id: Int): Product?
    suspend fun addProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(id: Int)
    suspend fun fetchAndStoreInitialProducts(limit: Int) // For initial fetch from API

    fun searchProducts(query: String): Flow<List<Product>>
    fun getProductsByCategory(category: String): Flow<List<Product>>
}