package com.example.rushbuy.feature.productList.domain.usecase

import androidx.paging.PagingData
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductsUseCase(
    private val repository: IProductRepository
) {
    operator fun invoke(): Flow<PagingData<Product>> {
        return repository.getProducts()
    }
}
class SearchProductsUseCase(
    private val repository: IProductRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Product>> {
        return repository.searchProducts(query)
    }
}