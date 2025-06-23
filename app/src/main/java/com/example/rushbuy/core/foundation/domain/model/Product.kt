package com.example.rushbuy.core.foundation.domain.model

data class Product (
    val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Double,
    val description: String,
    val ratings: Double,
    val category: String
)