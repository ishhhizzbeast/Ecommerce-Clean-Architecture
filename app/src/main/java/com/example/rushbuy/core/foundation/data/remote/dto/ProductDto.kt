package com.example.rushbuy.core.foundation.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class ProductDto(
    val id: Int,
    // Keep 'thumbnail' for now, or remove if you truly only want 'images'
    @SerializedName("thumbnail")
    val thumbnailUrl: String,
    val title: String,
    val price: Double,
    val description: String,
    val rating: Double,
    val category: String,
    @SerializedName("images") // Correctly map 'images' to a List<String>
    val images: List<String>
)

data class ProductListResponse(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)