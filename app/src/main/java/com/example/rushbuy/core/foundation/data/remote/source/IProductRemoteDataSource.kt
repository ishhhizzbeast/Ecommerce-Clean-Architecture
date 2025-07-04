package com.example.rushbuy.core.foundation.data.remote.source

import com.example.rushbuy.core.foundation.data.remote.dto.ProductDto
import com.example.rushbuy.core.foundation.data.remote.dto.ProductListResponse

interface IProductRemoteDataSource {
    suspend fun getProducts(limit: Int, skip: Int): ProductListResponse
    suspend fun getProductById(id: Int): ProductDto
    // Add other remote methods if needed (e.g., addProduct, updateProduct, deleteProduct on server)
    // suspend fun createProduct(productDto: ProductDto): ProductDto
    // suspend fun updateProduct(id: Int, productDto: ProductDto): ProductDto
    // suspend fun deleteProduct(id: Int): Unit
}