package com.example.rushbuy.core.foundation.data.remote.api

import com.example.rushbuy.core.foundation.data.remote.dto.ProductDto
import com.example.rushbuy.core.foundation.data.remote.dto.ProductListResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(@Query("limit") limit: Int,
                            @Query("skip") skip: Int = 0 ): ProductListResponse

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    @GET("products/categories")
    suspend fun getCategories(): Flow<List<String>>
}