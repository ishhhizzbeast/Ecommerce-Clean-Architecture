package com.example.rushbuy.feature.cart.domain.model

data class PaymentDetails(
    val amount: String,
    val productName: String,
    val productId: String,
    val callbackUrl: String,
    val clientID: String,
    val secretKey: String,
    val environment: String // Use ESewaConfiguration.ENVIRONMENT_TEST or .ENVIRONMENT_PRODUCTION
)