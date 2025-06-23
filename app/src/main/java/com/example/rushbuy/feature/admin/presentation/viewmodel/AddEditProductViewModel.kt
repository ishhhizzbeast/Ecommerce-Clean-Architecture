package com.example.rushbuy.feature.admin.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.domain.repository.IProductRepository
import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.feature.admin.domain.AddProductUseCase
import com.example.rushbuy.feature.admin.domain.UpdateProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditProductViewModel(
    private val productRepository: IProductRepository, // Used to fetch an existing product by ID
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val savedStateHandle: SavedStateHandle // Injected by Koin via koinViewModel() for nav args
) : ViewModel() {

    // State for the product being added or edited.
    // It will hold a 'Product' object if editing, or 'null' if adding a new one.
    private val _productToEdit = MutableStateFlow<ResultState<Product?>>(ResultState.Loading)
    val productToEdit: StateFlow<ResultState<Product?>> = _productToEdit.asStateFlow()

    // State for the result of the save/update operation
    private val _saveUpdateResult = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val saveUpdateResult: StateFlow<ResultState<Unit>> = _saveUpdateResult.asStateFlow()

    // Extract product ID from navigation arguments.
    // The key "productId" should match the argument name used in your navigation graph.
    internal val productId: String? = savedStateHandle["productId"]

    init {
        // Based on whether a productId is present, determine if we're adding or editing.
        productId?.let { id ->
            if (id.isNotBlank()) {
                // If productId is provided and not blank, it's edit mode: load the product
                loadProduct(id)
            } else {
                // If productId is provided but blank, treat as new product (e.g., from an implicit nav arg)
                _productToEdit.value = ResultState.Success(null)
            }
        } ?: run {
            // No productId means we are adding a new product
            _productToEdit.value = ResultState.Success(null)
        }
    }

    /**
     * Loads an existing product by its ID from the repository for editing.
     * Updates the [_productToEdit] StateFlow with loading, success (with product data), or error states.
     * @param id The ID of the product to load.
     */
    private fun loadProduct(id: String) {
        viewModelScope.launch {
            _productToEdit.value = ResultState.Loading // Set loading state
            try {
                val fetchedProduct = productRepository.getProductById(id.toInt())
                if (fetchedProduct != null) {
                    _productToEdit.value = ResultState.Success(fetchedProduct) // Product found
                } else {
                    _productToEdit.value = ResultState.Error("Product with ID '$id' not found.") // Product not found
                }
            } catch (e: Exception) {
                _productToEdit.value = ResultState.Error("Failed to load product: ${e.message ?: "Unknown error"}")
            }
        }
    }

    /**
     * Saves a product. It delegates to either [addProductUseCase] or [updateProductUseCase]
     * based on whether the product already has an ID.
     * Updates the [_saveUpdateResult] StateFlow with the operation's state.
     * @param product The [Product] object containing the details to save.
     */
    fun saveProduct(product: Product) {
        viewModelScope.launch {
            _saveUpdateResult.value = ResultState.Loading // Indicate save/update is in progress
            val result = if (product.id == 0) {
                // If product.id is blank, it's a new product
                addProductUseCase(product)
            } else {
                // If product.id is present, it's an existing product to update
                updateProductUseCase(product)
            }
            _saveUpdateResult.value = result // Update UI state with the result of the operation
        }
    }

    /**
     * Resets the [_saveUpdateResult] StateFlow to its initial success state.
     * This is useful after showing a success or error message to the user,
     * allowing them to perform another save operation or preventing the message from reappearing.
     */
    fun resetSaveUpdateResult() {
        _saveUpdateResult.value = ResultState.Success(Unit)
    }
}