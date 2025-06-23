package com.example.rushbuy.feature.admin.domain

import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.domain.repository.IProductRepository
import com.example.rushbuy.core.foundation.utils.ResultState
import kotlinx.coroutines.flow.Flow

class GetAdminProductsUseCase(
    private val productRepository: IProductRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return productRepository.getProducts()
    }
}

class AddProductUseCase(
    private val productRepository: IProductRepository
) {
    suspend operator fun invoke(product: Product): ResultState<Unit> {
        return try {
            productRepository.addProduct(product)
            ResultState.Success(Unit)
        } catch (e: Exception) {
            ResultState.Error("Failed to add product: ${e.message ?: "Unknown error"}")
        }
    }
}

class UpdateProductUseCase(
    private val productRepository: IProductRepository
) {
    suspend operator fun invoke(product: Product): ResultState<Unit> {
        return try {
            productRepository.updateProduct(product)
            ResultState.Success(Unit)
        } catch (e: Exception) {
            ResultState.Error("Failed to update product: ${e.message ?: "Unknown error"}")
        }
    }
}

class DeleteProductUseCase(
    private val productRepository: IProductRepository
) {
    suspend operator fun invoke(productId: Int): ResultState<Unit> {
        return try {
            productRepository.deleteProduct(productId)
            ResultState.Success(Unit)
        } catch (e: Exception) {
            ResultState.Error("Failed to delete product: ${e.message ?: "Unknown error"}")
        }
    }
}