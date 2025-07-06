package com.example.rushbuy.feature.Category.domain.usecase

import androidx.paging.PagingData
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductsByCategoryUseCase(
    private val productRepository: IProductRepository // Dependency on the product repository interface
) {
    /**
     * Invokes the use case to retrieve a flow of paginated products for the given category.
     *
     * @param category The name of the category to filter products by.
     * @return A [Flow] of [PagingData] containing [Product] objects that belong to the specified category.
     */
    operator fun invoke(category: String): Flow<PagingData<Product>> {
        // This is the core logic: directly calling the repository method
        return productRepository.getProductsByCategory(category)
    }
}