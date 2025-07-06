package com.example.rushbuy.core.foundation.data.remote.source

import com.example.rushbuy.core.foundation.data.remote.api.ProductApiService
import com.example.rushbuy.core.foundation.data.remote.dto.ProductDto
import com.example.rushbuy.core.foundation.data.remote.dto.ProductListResponse
import kotlinx.coroutines.flow.Flow

class ProductRemoteDataSourceImpl(private val apiService: ProductApiService) : IProductRemoteDataSource {
    override suspend fun getProducts(limit: Int, skip: Int): ProductListResponse {
        return apiService.getProducts(limit = limit, skip = skip)
    }

    override suspend fun getProductById(id: Int): ProductDto {
        return apiService.getProductById(id)
    }

    override suspend fun getAllCategories(): Flow<List<String>> {
        return apiService.getCategories()
    }
}