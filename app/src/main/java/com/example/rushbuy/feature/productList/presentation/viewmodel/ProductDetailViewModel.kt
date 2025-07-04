package com.example.rushbuy.feature.productList.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.domain.repository.IProductRepository // Assuming you have a repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: IProductRepository // Inject your repository here
) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> = _error

    fun getProductById(productId: Int) {
        _isLoading.value = true
        _error.value = null // Clear previous errors
        viewModelScope.launch {
            try {
                val fetchedProduct = productRepository.getProductById(productId) // Assuming this method exists
                _product.value = fetchedProduct
            } catch (e: Exception) {
                _error.value = e
                _product.value = null // Clear product on error
            } finally {
                _isLoading.value = false
            }
        }
    }
}