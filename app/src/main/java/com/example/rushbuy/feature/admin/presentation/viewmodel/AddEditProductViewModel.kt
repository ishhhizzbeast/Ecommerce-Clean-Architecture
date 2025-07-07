package com.example.rushbuy.feature.admin.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.domain.repository.IProductRepository
import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.feature.admin.domain.AddProductUseCase
import com.example.rushbuy.feature.admin.domain.UpdateProductUseCase
import com.example.rushbuy.feature.notification.domain.model.NewItemNotificationData
import com.example.rushbuy.feature.notification.domain.usecase.TriggerNewItemNotificationUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditProductViewModel(
    private val productRepository: IProductRepository, // Used to fetch an existing product by ID
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val savedStateHandle: SavedStateHandle, // Injected by Koin via koinViewModel() for nav args
    private val triggerNewItemNotificationUseCase: TriggerNewItemNotificationUseCase
) : ViewModel() {

    // Initial state for product to edit can also be Idle for consistency
    private val _productToEdit = MutableStateFlow<ResultState<Product?>>(ResultState.Idle) // Changed to Idle
    val productToEdit: StateFlow<ResultState<Product?>> = _productToEdit.asStateFlow()

    // THE KEY CHANGE: Initialize with ResultState.Idle
    private val _saveUpdateResult = MutableStateFlow<ResultState<Unit>>(ResultState.Idle) // <--- CHANGED HERE
    val saveUpdateResult: StateFlow<ResultState<Unit>> = _saveUpdateResult.asStateFlow()

    // NEW: SharedFlow to communicate CRUD events back to other screens (e.g., AdminProductListScreen)
    private val _productCrudEvent = MutableSharedFlow<ProductCrudEvent>()
    val productCrudEvent: SharedFlow<ProductCrudEvent> = _productCrudEvent.asSharedFlow()

    internal val productId: String? = savedStateHandle["productId"]

    init {
        productId?.let { id ->
            if (id.isNotBlank() && id != "0") { // Check for valid ID for edit mode
                loadProduct(id)
            } else {
                // If ID is 0 or blank, it's a new product, no need to load
                _productToEdit.value = ResultState.Success(null)
            }
        } ?: run {
            // No product ID, definitely a new product
            _productToEdit.value = ResultState.Success(null)
        }
    }

    private fun loadProduct(id: String) {
        viewModelScope.launch {
            _productToEdit.value = ResultState.Loading
            try {
                val fetchedProduct = productRepository.getProductById(id.toInt())
                if (fetchedProduct != null) {
                    _productToEdit.value = ResultState.Success(fetchedProduct)
                } else {
                    _productToEdit.value = ResultState.Error("Product with ID '$id' not found.")
                }
            } catch (e: Exception) {
                _productToEdit.value = ResultState.Error("Failed to load product: ${e.message ?: "Unknown error"}")
            }
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            _saveUpdateResult.value = ResultState.Loading // Set to Loading before operation
            val result: ResultState<Unit>

            if (product.id == 0) { // If it's a new product
                val addResult = addProductUseCase(product)

                if (addResult is ResultState.Success) {
                    val addedProduct = addResult.data
                    Log.d("AddEditProductVM", "Product added successfully. ID=${addedProduct.id}, Name=${addedProduct.name}. Triggering notification.")
                    triggerNewItemNotificationUseCase(
                        NewItemNotificationData(
                            id = addedProduct.id,
                            name = addedProduct.name
                        )
                    )
                    result = ResultState.Success(Unit)
                    // NEW: Emit ProductAdded event
                    _productCrudEvent.emit(ProductCrudEvent.ProductAdded) // No product ID needed for ProductAdded
                } else {
                    Log.e("AddEditProductVM", "Failed to add product: ${(addResult as ResultState.Error).message}")
                    result = addResult as ResultState.Error
                }
            } else { // If it's an existing product (update)
                // updateProductUseCase returns ResultState<Unit> directly
                result = updateProductUseCase(product)
                // NEW: Emit ProductUpdated event on success
                if (result is ResultState.Success) {
                    _productCrudEvent.emit(ProductCrudEvent.ProductUpdated(product.id))
                }
            }
            _saveUpdateResult.value = result // Update the StateFlow to Success or Error
        }
    }

    // Reset the result state to Idle after a UI action (like showing snackbar/navigating)
    fun resetSaveUpdateResult() {
        _saveUpdateResult.value = ResultState.Idle // <--- CHANGED HERE
    }
}

// Ensure this sealed class is accessible (either here or in its own file like ProductCrudEvent.kt)
// It's good practice to have this in its own dedicated file.
sealed class ProductCrudEvent {
    object ProductAdded : ProductCrudEvent()
    data class ProductUpdated(val productId: Int) : ProductCrudEvent()
    data class ProductDeleted(val productId: Int) : ProductCrudEvent()
}