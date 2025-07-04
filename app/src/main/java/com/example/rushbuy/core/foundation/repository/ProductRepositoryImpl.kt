package com.example.rushbuy.core.foundation.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.rushbuy.core.foundation.data.local.dao.ProductDao
import com.example.rushbuy.core.foundation.data.local.source.IProductLocalDataSource
import com.example.rushbuy.core.foundation.data.paging.ProductPagingSource
import com.example.rushbuy.core.foundation.data.remote.api.ProductApiService
import com.example.rushbuy.core.foundation.data.remote.source.IProductRemoteDataSource
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
class ProductRepositoryImpl(
    // CHANGED: Inject data sources instead of DAO and API service directly
    private val remoteDataSource: IProductRemoteDataSource,
    private val localDataSource: IProductLocalDataSource
) : IProductRepository {

    // CHANGED: Now uses Pager and ProductPagingSource for paginated data
    override fun getProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10, // Define your page size, should match what your API and PagingSource can handle
                enablePlaceholders = false // Set to true if your PagingSource handles nulls and placeholders
            ),
            pagingSourceFactory = {
                // The PagingSource orchestrates fetching from remote and storing in local
                ProductPagingSource(remoteDataSource, localDataSource)
            }
        ).flow
    }

    // CHANGED: Uses local and then remote data source for single product by ID
    override suspend fun getProductById(id: Int): Product? {
        // Try local cache first
        val localProduct = localDataSource.getProductById(id)
        if (localProduct != null) {
            return localProduct
        }
        // If not in cache, fetch from remote, store, and return
        return try {
            val remoteDto = remoteDataSource.getProductById(id)
            val productDomain = remoteDto.toDomain()
            localDataSource.insertProducts(listOf(productDomain)) // Insert into cache as a list
            productDomain
        } catch (e: Exception) {
            // TODO: Log the error (e.g., Timber.e(e, "Error fetching product by ID from remote"))
            // Consider rethrowing a custom domain-specific exception if needed
            null
        }
    }

    // CHANGED: Uses local data source for adding. Remote API integration is a TODO.
    override suspend fun addProduct(product: Product): Product {
        // TODO: In a real application, you would typically add to the remote API first,
        // then insert the server-returned product (which might have a new ID) into local cache.
        // For now, it only adds to local cache.
        localDataSource.insertProducts(listOf(product))
        return product // Assuming the 'product' object already has its ID set if it's new
    }

    // CHANGED: Uses local data source for updating. Remote API integration is a TODO.
    override suspend fun updateProduct(product: Product) {
        // TODO: Similar to addProduct, consider updating on remote API first.
        localDataSource.insertProducts(listOf(product)) // insertProducts with REPLACE strategy acts as update
    }

    // CHANGED: Uses local data source for deleting. Remote API integration is a TODO.
    override suspend fun deleteProduct(id: Int) {
        // TODO: Similar to addProduct, consider deleting on remote API first.
        localDataSource.deleteProductById(id)
    }

    override suspend fun fetchAndStoreInitialProducts(limit: Int) {
        TODO("Not yet implemented")
    }

    // REMOVED: This method is no longer needed.
    // The ProductPagingSource now handles initial fetching and caching from remote
    // when the local cache is empty or needs refreshing.
    // override suspend fun fetchAndStoreInitialProducts(limit: Int) { ... }

    // CHANGED: Now uses Pager and ProductPagingSource for paginated search results
    override fun searchProducts(query: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                // Pass the query to the PagingSource so it can filter results
                ProductPagingSource(remoteDataSource, localDataSource, query = query)
            }
        ).flow
    }

    // CHANGED: Now uses Pager and ProductPagingSource for paginated category results
    override fun getProductsByCategory(category: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                // Assuming ProductPagingSource can filter by category using the 'query' parameter
                // If category filtering has significantly different logic, you might consider a separate PagingSource
                ProductPagingSource(remoteDataSource, localDataSource, category = category)
            }
        ).flow
    }
}