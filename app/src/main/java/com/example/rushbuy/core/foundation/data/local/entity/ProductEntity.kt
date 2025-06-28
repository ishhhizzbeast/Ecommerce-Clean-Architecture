package com.example.rushbuy.core.foundation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Double,
    val description: String,
    val ratings: Double,
    val category: String
)