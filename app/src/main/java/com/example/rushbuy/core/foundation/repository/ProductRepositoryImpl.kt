package com.example.rushbuy.core.foundation.repository

import com.example.rushbuy.core.foundation.data.local.dao.ProductDao
import com.example.rushbuy.core.foundation.data.remote.api.ProductApiService
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.domain.repository.IProductRepository
import com.example.rushbuy.core.foundation.utils.toDomain
import com.example.rushbuy.core.foundation.utils.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of IProductRepository.
 * This class handles data sourcing logic, deciding whether to fetch from local DB or remote API,
 * and performing necessary data transformations.
 *
 * It acts as the single source of truth for product data for the rest of the application.
 */
class ProductRepositoryImpl( // Removed @Inject
    private val productDao: ProductDao,
    private val productApiService: ProductApiService
) : IProductRepository {

    override fun getProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)?.toDomain()
    }

    override suspend fun addProduct(product: Product): Product {
        val generatedIdLong = productDao.insertProduct(product.toEntity()) // Get the generated ID
        val newProduct = product.copy(id = generatedIdLong.toInt()) // Create new Product with ID
        return newProduct
        // TODO: In a real application, you might also push this to a remote API
        // if the admin's additions need to be synchronized server-side.
        // This would involve calling productApiService.createProduct(product.toDto())
    }

    override suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.toEntity())
        // TODO: Similar to addProduct, consider updating on remote API if necessary.
    }

    override suspend fun deleteProduct(id: Int) {
        productDao.deleteProduct(id)
        // TODO: Similar to addProduct, consider deleting on remote API if necessary.
    }

    override suspend fun fetchAndStoreInitialProducts(limit: Int) {
        val productCount = productDao.getProductCount()
        if (productCount == 0) {
            try {
                val remoteProducts = productApiService.getProducts(limit).products
                val productEntities = remoteProducts.map { it.toEntity() }
                productDao.insertAllProducts(productEntities)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        return productDao.searchProducts(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getProductsByCategory(category: String): Flow<List<Product>> {
        return productDao.getProductsByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}