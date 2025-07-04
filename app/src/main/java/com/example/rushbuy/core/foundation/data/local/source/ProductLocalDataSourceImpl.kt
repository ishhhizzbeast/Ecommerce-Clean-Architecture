package com.example.rushbuy.core.foundation.data.local.source

import com.example.rushbuy.core.foundation.data.local.dao.ProductDao
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.utils.toDomain
import com.example.rushbuy.core.foundation.utils.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class ProductLocalDataSourceImpl(private val productDao: ProductDao) : IProductLocalDataSource {
    override fun getAllProducts(): Flow<List<Product>> =
        productDao.getAllProducts().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getProductById(productId: Int): Product? =
        productDao.getProductById(productId)?.toDomain()

    override suspend fun insertProducts(products: List<Product>) =
        productDao.insertAllProducts(products.map { it.toEntity() }) // Maps domain to entity for Room

    override suspend fun clearAllProducts() = productDao.clearAllProducts()

    override fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchProducts(query).map { entities -> entities.map { it.toDomain() } }

    override suspend fun deleteProductById(productId: Int) {
        productDao.deleteProduct(productId)
    }

    override suspend fun getProductCount(): Int {
        return productDao.getProductCount()
    }
}