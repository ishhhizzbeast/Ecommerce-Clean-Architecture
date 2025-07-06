package com.example.rushbuy.feature.cart.presentation.ui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.rushbuy.feature.cart.domain.model.CartItem
import com.example.rushbuy.feature.cart.presentation.viewmodel.CartViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.rushbuy.R // Ensure this import points to your project's R file
import com.f1soft.esewapaymentsdk.EsewaConfiguration
import com.f1soft.esewapaymentsdk.EsewaPayment
import com.f1soft.esewapaymentsdk.ui.screens.EsewaPaymentActivity
import kotlin.jvm.java


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = koinViewModel() // Inject CartViewModel using Koin
) {
    // Observe the Flow of cart items and convert to State for UI updates
    val cartItems by viewModel.cartItems.collectAsState(initial = emptyList())
    // Observe the Flow of subtotal
    val subtotal by viewModel.subtotal.collectAsState(initial = 0.0)
    // Observe the Flow of checkout success
    val checkoutSuccess by viewModel.checkoutSuccess.collectAsState(initial = false)

    val context = LocalContext.current

    // Launcher for eSewa payment activity
    val esewaPaymentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val message = data.getStringExtra(EsewaPayment.EXTRA_RESULT_MESSAGE)
                Toast.makeText(context, "Payment SUCCESS: $message", Toast.LENGTH_LONG).show()

                // IMPORTANT: Now, you would call your backend to verify this transaction.
                // For demonstration, we'll assume success and clear cart.
                // In a real app, this should only happen AFTER server-side verification.
                viewModel.onClearCart() // Clear cart on client-side success (assuming server verification follows)
                viewModel.resetCheckoutSuccessState() // Reset if you have a success screen after checkout
                // Optionally navigate to an order confirmation screen
                // navController.navigate("order_confirmation_route")

            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(context, "Payment CANCELED by user.", Toast.LENGTH_SHORT).show()
        } else if (result.resultCode == EsewaPayment.RESULT_EXTRAS_INVALID) {
            // Handle invalid parameters or configuration errors
            val message = result.data?.getStringExtra(EsewaPayment.EXTRA_RESULT_MESSAGE) ?: "Invalid eSewa parameters."
            Toast.makeText(context, "Payment Error: $message", Toast.LENGTH_LONG).show()
        } else {
            // Handle other error codes if eSewa SDK provides them
            Toast.makeText(context, "Payment failed with code: ${result.resultCode}", Toast.LENGTH_LONG).show()
        }
    }

    // LaunchedEffect to observe payment initiation events from ViewModel
    LaunchedEffect(Unit) { // Use Unit as key to launch once
        viewModel.paymentInitiationEvent.collect { paymentDetails ->
            try {
                // Configure eSewa
                val eSewaConfiguration = EsewaConfiguration(
                    paymentDetails.clientID,
                    paymentDetails.secretKey,
                    paymentDetails.environment // This should be a String, not an Int constant
                )// ENVIRONMENT_TEST or ENVIRONMENT_PRODUCTION

                // Create payment object
                val eSewaPayment = EsewaPayment(
                    paymentDetails.amount,
                    paymentDetails.productName,
                    paymentDetails.productId,
                    paymentDetails.callbackUrl
                )

                // Create intent to launch eSewa payment activity
                val intent = Intent(context, EsewaPaymentActivity::class.java).apply {
                    putExtra(EsewaConfiguration.ESEWA_CONFIGURATION, eSewaConfiguration)
                    putExtra(EsewaPayment.ESEWA_PAYMENT, eSewaPayment)
                }

                // Launch the activity for result
                esewaPaymentLauncher.launch(intent)

            } catch (e: Exception) {
                // Catch any errors during intent creation or missing SDK
                Toast.makeText(context, "Error initiating payment: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    // Effect for showing internal checkout success message (can be removed if replaced by eSewa success flow)
    LaunchedEffect(checkoutSuccess) {
        if (checkoutSuccess) {
            Toast.makeText(context, "Internal checkout logic successful!", Toast.LENGTH_LONG).show()
            // This might be for a scenario where payment is not external.
            // For eSewa, you'd usually rely on the launcher's result.
            // viewModel.resetCheckoutSuccessState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CartSummaryCard(
                    subtotal = subtotal,
                    // Now, onProceedToCheckout will trigger the payment flow in the ViewModel
                    onProceedToCheckout = viewModel::onProceedToCheckout,
                    modifier = Modifier.padding(16.dp),
                    isCartEmpty = cartItems.isEmpty()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cartItems.isEmpty()) {
                EmptyCartMessage(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // Takes up remaining space
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems, key = { it.productId }) { item ->
                        CartItemCard(
                            cartItem = item,
                            onQuantityChange = viewModel::onUpdateQuantity,
                            onRemoveItem = viewModel::onRemoveFromCart
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCartMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Your cart is empty!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Start adding some amazing products.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (String, Int) -> Unit, // productId, newQuantity
    onRemoveItem: (String) -> Unit, // productId
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = cartItem.imageUrl,
                contentDescription = cartItem.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.active), // Use your placeholder
                error = painterResource(id = R.drawable.active) // Use your error image
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Product Name
                Text(
                    text = cartItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Price per item
                Text(
                    text = "$${String.format("%.2f", cartItem.price)} / item",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onQuantityChange(cartItem.productId, cartItem.quantity - 1) },
                        enabled = cartItem.quantity > 0 // Disable if quantity is 0
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
                    }
                    Text(
                        text = "${cartItem.quantity}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(
                        onClick = { onQuantityChange(cartItem.productId, cartItem.quantity + 1) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase quantity")
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.End) {
                // Total price for this item
                Text(
                    text = "$${String.format("%.2f", cartItem.totalPrice)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary // Use primary color for total
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Remove button
                IconButton(
                    onClick = { onRemoveItem(cartItem.productId) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove item",
                        tint = MaterialTheme.colorScheme.error // Use error color for delete
                    )
                }
            }
        }
    }
}

@Composable
fun CartSummaryCard(
    subtotal: Double,
    onProceedToCheckout: () -> Unit,
    modifier: Modifier = Modifier,
    isCartEmpty: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Higher elevation for a floating effect
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtotal:",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$${String.format("%.2f", subtotal)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onProceedToCheckout,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCartEmpty // Disable checkout button if cart is empty
            ) {
                Text(text = "Proceed to Checkout", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewCartScreen() {
    MaterialTheme {
        // For Preview, we need a dummy NavController
        CartScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCartItemCard() {
    MaterialTheme {
        CartItemCard(
            cartItem = CartItem(
                productId = "p1",
                name = "Organic Apples (1kg)",
                imageUrl = "https://via.placeholder.com/150",
                price = 2.99,
                quantity = 2
            ),
            onQuantityChange = { _, _ -> },
            onRemoveItem = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCartSummaryCard() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CartSummaryCard(subtotal = 54.98, onProceedToCheckout = {}, isCartEmpty = false)
            Spacer(modifier = Modifier.height(16.dp))
            CartSummaryCard(subtotal = 0.0, onProceedToCheckout = {}, isCartEmpty = true)
        }
    }
}