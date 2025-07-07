package com.example.rushbuy.core.foundation.utils

//import androidx. compose.ui.graphics.Color

import com.example.rushbuy.core.foundation.data.local.entity.ProductEntity
import com.example.rushbuy.core.foundation.data.remote.dto.ProductDto
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.feature.profile.domain.model.UserProfile
import com.google.firebase.auth.FirebaseUser

fun ProductDto.toDomain(): Product {
    return Product(
        id = this.id,
        imageUrl = this.thumbnailUrl,
        name = this.title, // 'title' from API maps to 'name' in domain model
        price = this.price,
        description = this.description,
        ratings = this.rating, // 'rating' from API maps to 'ratings' in domain model
        category = this.category
    )
}

/**
 * Converts a ProductDto (from remote API) to a ProductEntity (for local database).
 */
fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = this.id,
        imageUrl = this.thumbnailUrl,
        name = this.title, // 'title' from API maps to 'name' in entity
        price = this.price,
        description = this.description,
        ratings = this.rating, // 'rating' from API maps to 'ratings' in entity
        category = this.category
    )
}

// --- Mappers from Entity (Local Database) to Domain Model ---

/**
 * Converts a ProductEntity (from local database) to a Product domain model.
 */
fun ProductEntity.toDomain(): Product {
    return Product(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        description = this.description,
        ratings = this.ratings,
        category = this.category
    )
}

// --- Mappers from Domain Model to Entity (for storage) ---

/**
 * Converts a Product domain model to a ProductEntity (for local database storage).
 */
fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        description = this.description,
        ratings = this.ratings,
        category = this.category
    )
}

fun FirebaseUser.toDomainUserProfile(): UserProfile {
    return UserProfile(
        uid = uid,
        displayName = displayName,
        email = email
        // Removed: profilePictureUrl = photoUrl?.toString()
    )
}

// TODO: If you ever need to send a Product domain model back to the API (e.g., for admin updates),
// you would add a Product.toDto() mapper here as well.
// fun Product.toDto(): ProductDto { ... }


