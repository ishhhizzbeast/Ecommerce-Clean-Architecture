package com.example.rushbuy.feature.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.feature.admin.domain.DeleteProductUseCase
import com.example.rushbuy.feature.admin.domain.GetAdminProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AdminHomeViewModel(
    private val getAdminProductsUseCase: GetAdminProductsUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    // State for the list of products displayed on the home screen
    private val _products = MutableStateFlow<ResultState<List<Product>>>(ResultState.Loading)
    val products: StateFlow<ResultState<List<Product>>> = _products.asStateFlow()

    // State for the result of a product deletion operation
    private val _deleteProductResult = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val deleteProductResult: StateFlow<ResultState<Unit>> = _deleteProductResult.asStateFlow()

    init {
        // Fetch products immediately when the ViewModel is initialized
        getProducts()
    }

    /**
     * Fetches the list of products for the admin home screen.
     * Updates the [_products] StateFlow with loading, success, or error states.
     */
    fun getProducts() {
        _products.value = ResultState.Loading // Set loading state before fetching
        getAdminProductsUseCase()
            .onEach { productsList ->
                _products.value = ResultState.Success(productsList) // Update with successful data
            }
            .catch { e ->
                // Catch any exceptions from the flow (e.g., from repository/data source)
                _products.value = ResultState.Error(e.message ?: "Unknown error fetching products")
            }
            .launchIn(viewModelScope) // Collects the flow within the ViewModel's lifecycle
    }

    /**
     * Initiates the deletion of a product.
     * Updates [_deleteProductResult] with the operation's state (loading, success, error).
     * @param productId The ID of the product to delete (Int as per your UseCase).
     */
    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            _deleteProductResult.value = ResultState.Loading // Indicate deletion is in progress
            _deleteProductResult.value = deleteProductUseCase(productId) // Execute the UseCase

            // If deletion was successful, refresh the product list to reflect the change
            if (_deleteProductResult.value is ResultState.Success) {
                getProducts()
            }
            // If it was an error, the error state will be held in _deleteProductResult for UI to react
        }
    }
}