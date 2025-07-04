package com.example.rushbuy.feature.productList.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rushbuy.core.foundation.domain.model.Product // Correct Product path
import com.example.rushbuy.feature.productList.domain.usecase.GetProductsUseCase
import com.example.rushbuy.feature.productList.domain.usecase.SearchProductsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce // For search debounce
import kotlinx.coroutines.flow.distinctUntilChanged // For search distinct changes
import kotlinx.coroutines.flow.flatMapLatest // For search
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    // State for the search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: Flow<String> = _searchQuery

    // Flow for the list of products, which can change based on search
    val products: Flow<PagingData<Product>> = _searchQuery
        .debounce(300L) // Debounce search queries to avoid too many API calls
        .distinctUntilChanged() // Only emit if query actually changes
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getProductsUseCase() // Get all products if search query is empty
            } else {
                searchProductsUseCase(query) // Search products if query is not empty
            }
        }
        .cachedIn(viewModelScope) // Cache PagingData for configuration changes

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // You can add other ViewModel functions here, e.g., for filtering by category
    // fun filterByCategory(category: String) { ... }
}