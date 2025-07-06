package com.example.rushbuy.feature.Category.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.domain.repository.IProductRepository
import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.feature.Category.domain.model.Category
import com.example.rushbuy.feature.Category.domain.usecase.GetProductsByCategoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalCoroutinesApi::class) // Added OptIn for launchIn
class CategoryViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val productRepository: IProductRepository
) : ViewModel() {

    // StateFlow to hold the currently selected category name
    private val _currentSelectedCategory = MutableStateFlow(
        savedStateHandle.get<String>("category") ?: ""
    )
    val currentSelectedCategory: StateFlow<String> = _currentSelectedCategory.asStateFlow()

    // StateFlow to hold all available categories, wrapped in a ResultState for UI state management
    private val _allAvailableCategories = MutableStateFlow<ResultState<List<Category>>>(ResultState.Idle)
    val allAvailableCategories: StateFlow<ResultState<List<Category>>> = _allAvailableCategories.asStateFlow()

    // Flow of paginated products, filtered by the current selected category
    val products: Flow<PagingData<Product>> = _currentSelectedCategory
        .flatMapLatest { category ->
            if (category.isNotBlank()) {
                getProductsByCategoryUseCase(category)
            } else {
                productRepository.getProducts() // If no category selected, show all products
            }
        }
        .cachedIn(viewModelScope) // Cache PagingData in the ViewModel's scope

    init {
        // Fetch all categories for the filter dropdown from the repository
        // Emit Loading state before starting the fetch
        _allAvailableCategories.value = ResultState.Loading
        productRepository.getAllCategories()
            .map { categoryNames ->
                // Map the List<String> of category names from the repository
                // into a List<Category> domain model for the UI
                ResultState.Success(categoryNames.map { Category(name = it) })
            }
            .catch { e ->
                // Catch any errors during the fetch and emit an Error state
                _allAvailableCategories.value = ResultState.Error(
                    "Failed to fetch categories for filter: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
            .onEach { result ->
                // Update the _allAvailableCategories StateFlow with the latest result
                _allAvailableCategories.value = result

                // Optional: Logic to set a default category if none is selected initially
                if (_currentSelectedCategory.value.isBlank() && result is ResultState.Success && result.data.isNotEmpty()) {
                    // Example: Set the first category as the default if desired
                    // _currentSelectedCategory.value = result.data.first().name
                    // For now, it explicitly remains blank to show "All Products" by default
                    // when no category is selected from navigation.
                }
            }.launchIn(viewModelScope) // Launch the Flow collection in the ViewModel's scope
    }

    /**
     * Called when a user selects a category from the filter dropdown.
     * Updates the current selected category and saves it to SavedStateHandle.
     */
    fun onCategorySelected(categoryName: String) {
        _currentSelectedCategory.value = categoryName
        savedStateHandle["category"] = categoryName
    }

    /**
     * Clears the currently applied category filter, effectively showing all products.
     */
    fun clearCategoryFilter() {
        _currentSelectedCategory.value = ""
        savedStateHandle["category"] = ""
    }
}