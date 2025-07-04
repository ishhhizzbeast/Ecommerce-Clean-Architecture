package com.example.rushbuy.feature.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.feature.admin.domain.DeleteProductUseCase
import com.example.rushbuy.feature.admin.domain.GetAdminProductsUseCase
import kotlinx.coroutines.flow.Flow
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

    // Provides a Flow of PagingData for the list of products.
    // The `cachedIn` operator ensures data persistence across configuration changes.
    val products: Flow<PagingData<Product>> = getAdminProductsUseCase()
        .cachedIn(viewModelScope)

    // State for the result of a product deletion operation.
    private val _deleteProductResult = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val deleteProductResult: StateFlow<ResultState<Unit>> = _deleteProductResult.asStateFlow()

    /**
     * Initiates the deletion of a product.
     * Updates [_deleteProductResult] with the operation's state (loading, success, error).
     * The UI layer should observe [_deleteProductResult] and, upon successful deletion,
     * call `LazyPagingItems.refresh()` to update the product list.
     *
     * @param productId The ID of the product to delete.
     */
    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            _deleteProductResult.value = ResultState.Loading
            _deleteProductResult.value = deleteProductUseCase(productId)
        }
    }
}