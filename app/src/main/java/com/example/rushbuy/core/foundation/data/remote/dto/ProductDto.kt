package com.example.rushbuy.core.foundation.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class ProductDto(
    val id: Int,
    @SerializedName("thumbnail")
    val imageUrl: String,
    val title: String,
    val price: Double,
    val description: String,
    val rating: Double,
    val category: String
    // Add other fields from the API response if needed
)

data class ProductListResponse(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)