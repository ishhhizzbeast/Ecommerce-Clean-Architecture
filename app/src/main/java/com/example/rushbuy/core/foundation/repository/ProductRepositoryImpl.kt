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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
    // A CoroutineScope for launching background tasks within the repository.
    // In a production app, consider injecting an ApplicationScope or using a Worker for long-lived operations.
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
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

    override fun getAllCategories(): Flow<List<String>> = flow {
        // 1. Start by emitting categories currently in the local database.
        // This ensures the UI gets data as fast as possible if cached data exists.
        localDataSource.getAllCategories()
            .distinctUntilChanged() // Only emit if the list of categories actually changes
            .collect { emit(it) } // Continuously re-emit local categories as they change

        // 2. Separately, trigger a refresh from the remote API in a background coroutine.
        // The success of this refresh (by inserting new products) will update the local database,
        // which then causes the localDataSource.getAllCategories() flow (being collected above)
        // to automatically emit the updated list of categories.
        repositoryScope.launch {
            try {
                // Fetch products from the remote to implicitly update categories in local DB.
                // We're using getProducts(limit=100) here as a simple way to refresh categories.
                // In a more robust system, if your API has a dedicated endpoint that
                // returns *all* categories (e.g., /products/categories) and you have
                // a CategoryEntity/CategoryDao, you would use remoteDataSource.getCategories()
                // and then insert/update those specific CategoryEntities.
                val remoteProductListResponse = remoteDataSource.getProducts(limit = 100, skip = 0) // Fetch some initial products
                val remoteProducts = remoteProductListResponse.products.map { it.toDomain() }

                // This aggressive strategy clears and re-inserts. Be mindful of potential data loss
                // if other parts of the app rely on older product data during this brief clear.
                // A more robust approach might be to compute a diff and perform targeted updates.
                localDataSource.clearAllProducts() // Clear existing product cache
                localDataSource.insertProducts(remoteProducts) // Insert fresh product data
                println("Products (and derived categories) refreshed from remote successfully.")

            } catch (e: Exception) {
                  println("Error refreshing products/categories from remote: ${e.message}")
            }
        }
    }


}