package com.example.rushbuy.feature.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushbuy.feature.cart.domain.model.CartItem
import com.example.rushbuy.feature.cart.domain.model.PaymentDetails
import com.example.rushbuy.feature.cart.domain.usecase.AddToCartUseCase
import com.example.rushbuy.feature.cart.domain.usecase.ClearCartUseCase
import com.example.rushbuy.feature.cart.domain.usecase.GetCartItemsUseCase
import com.example.rushbuy.feature.cart.domain.usecase.RemoveFromCartUseCase
import com.example.rushbuy.feature.cart.domain.usecase.UpdateCartItemQuantityUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import com.f1soft.esewapaymentsdk.EsewaConfiguration


class CartViewModel(
    // Inject all necessary use cases via the constructor (Koin will handle this)
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val addToCartUseCase: AddToCartUseCase, // Used for adding from ProductDetail/ProductList
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    // MutableStateFlow to hold the list of items in the cart.
    // It's private to control updates from within the ViewModel.
    private val _cartItems: MutableStateFlow<List<CartItem>> = MutableStateFlow(emptyList())
    // Publicly exposed as a generic Flow. This bypasses the asStateFlow() extension.
    val cartItems: Flow<List<CartItem>> = _cartItems

    // Exposed as a generic Flow. This bypasses the asStateFlow() extension.
    val subtotal: Flow<Double> = _cartItems.map { items ->
        items.sumOf { it.totalPrice }
    } // Removed .asStateFlow()

    private val _paymentInitiationEvent = MutableSharedFlow<PaymentDetails>()
    val paymentInitiationEvent: SharedFlow<PaymentDetails> = _paymentInitiationEvent.asSharedFlow()
    // StateFlow to indicate if the checkout process was successful.
    // This can be used to trigger navigation or show a success message.
    private val _checkoutSuccess = MutableStateFlow(false)
    // Exposed as a generic Flow. This bypasses the asStateFlow() extension.
    val checkoutSuccess: Flow<Boolean> = _checkoutSuccess // Removed .asStateFlow()

    init {
        // Collect cart items from the GetCartItemsUseCase whenever the ViewModel is initialized.
        // This keeps _cartItems up-to-date with the repository's state.
        viewModelScope.launch {
            getCartItemsUseCase().collect { items ->
                _cartItems.value = items
            }
        }
    }

    /**
     * Adds a product to the cart. This function would typically be called
     * from ProductListScreen or ProductDetailScreen.
     */
    fun onAddToCart(productId: String, name: String, imageUrl: String, price: Double, quantity: Int = 1) {
        viewModelScope.launch {
            addToCartUseCase(productId, name, imageUrl, price, quantity)
        }
    }

    /**
     * Updates the quantity of a specific item in the cart.
     * Called from the CartScreen when user changes quantity.
     */
    fun onUpdateQuantity(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            updateCartItemQuantityUseCase(productId, newQuantity)
        }
    }

    /**
     * Removes a specific item from the cart.
     * Called from the CartScreen when user clicks remove.
     */
    fun onRemoveFromCart(productId: String) {
        viewModelScope.launch {
            removeFromCartUseCase(productId)
        }
    }

    /**
     * Clears all items from the cart.
     * Could be called after a successful checkout.
     */
    fun onClearCart() {
        viewModelScope.launch {
            clearCartUseCase()
        }
    }

    /**
     * Initiates the checkout process.
     * This is where the integration with Esewa will primarily happen.
     */
    fun onProceedToCheckout() {
        viewModelScope.launch {
            val currentSubtotal = _cartItems.value.sumOf { it.totalPrice }

            // Generate a unique transaction UUID if needed for verification later
            val transactionUuid = UUID.randomUUID().toString()

            // Dummy values for now - REPLACE with your actual eSewa merchant credentials
            val esewaClientId =
                "JB0BBQ4aD0UqIThFJwAKBgAXEUkEGQUBBAwdOgABHD4DChwUAB0R" // Test Client ID
            val esewaSecretKey = "BhwIWQQADhIYSxILExMcAgFXFhcOBwAKBgAXEQ==" // Test Secret Key
            val esewaCallbackUrl =
                "http://example.com/esewa_payment_callback" // Your app's backend callback URL

            // Emit the payment details to the UI
            _paymentInitiationEvent.emit(
                PaymentDetails(
                    amount = String.format("%.2f", currentSubtotal),
                    productName = "RushBuy Order", // Or a more specific name like "Items from RushBuy"
                    productId = transactionUuid, // Using UUID as unique order ID for eSewa
                    callbackUrl = esewaCallbackUrl,
                    clientID = esewaClientId,
                    secretKey = esewaSecretKey,
                    environment = EsewaConfiguration.ENVIRONMENT_TEST // Change to .ENVIRONMENT_PRODUCTION for live
                )
            )

            // No clearCart() here yet. Clear it ONLY after successful payment confirmation.
            // _checkoutSuccess.value = true // Will be set after actual payment success
            // onClearCart() // Clear cart after actual payment success
        }
    }

    /**
     * Resets the checkout success state. Useful if navigating away from the cart
     * and want to prevent immediate re-triggering of success UI.
     */
    fun resetCheckoutSuccessState() {
        _checkoutSuccess.value = false
    }
}