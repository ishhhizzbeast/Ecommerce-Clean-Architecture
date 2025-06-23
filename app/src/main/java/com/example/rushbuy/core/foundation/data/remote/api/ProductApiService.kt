package com.example.rushbuy.core.foundation.data.remote.api

import com.example.rushbuy.core.foundation.data.remote.dto.ProductDto
import com.example.rushbuy.core.foundation.data.remote.dto.ProductListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(@Query("limit") limit: Int): ProductListResponse

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto
}