package com.example.rushbuy.feature.admin.presentation.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.feature.admin.presentation.viewmodel.AddEditProductViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavController,
    productId: String? // This will now correctly be null when adding a new product due to AdminRoutes change
) {
    val viewModel: AddEditProductViewModel = koinViewModel()

    val productState by viewModel.productToEdit.collectAsState()
    val saveResultState by viewModel.saveUpdateResult.collectAsState()

    // Internal mutable states for form fields
    // REMOVE THIS LINE: var id by rememberSaveable { mutableStateOf(productId ?: "") }
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var rating by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    // --- LaunchedEffect to handle initial product loading / form resetting ---
    LaunchedEffect(viewModel.productId) { // Trigger when ViewModel's internal productId is set
        // Use viewModel.productId directly as it's correctly managed by SavedStateHandle
        if (!viewModel.productId.isNullOrBlank()) {
            // It's an edit scenario, wait for productToEdit state to populate fields
            // No direct loadProduct call here, it's handled in ViewModel's init.
            // This LaunchedEffect will primarily react to productState changes.
        } else {
            // It's an add scenario or initial state, reset fields
            name = ""
            description = ""
            price = ""
            imageUrl = ""
            category = ""
            rating = ""
        }
    }


    // Effect to update form fields when productToEdit changes (for edit mode)
    // This effect ensures the UI fields reflect the loaded product.
    LaunchedEffect(productState) {
        if (productState is ResultState.Success) {
            val product = (productState as ResultState.Success).data
            product?.let {
                // Ensure the Product data class has all these fields
                name = it.name
                description = it.description
                price = it.price.toString()
                imageUrl = it.imageUrl
                category = it.category
                rating = it.ratings.toString() // Assuming Product has a 'ratings' field
            }
        } else if (productState is ResultState.Error) {
            snackbarHostState.showSnackbar(
                message = "Failed to load product: ${(productState as ResultState.Error).message}",
                withDismissAction = true
            )
            // Optionally navigate back if product loading failed critically for edit mode
            // navController.popBackStack()
        }
    }

    // Effect for handling saveResultState (navigation on success, snackbar on error)
    LaunchedEffect(saveResultState) {
        when (saveResultState) {
            is ResultState.Success -> {
                // Check if it's not the initial Unit state, meaning a save operation occurred
                if ((saveResultState as ResultState.Success).data != Unit) { // More robust check for actual save completion
                    snackbarHostState.showSnackbar(
                        message = "Product saved successfully!",
                        duration = SnackbarDuration.Short
                    )
                    // Navigate back to the admin home screen after successful save
                    navController.popBackStack()
                    viewModel.resetSaveUpdateResult() // Reset ViewModel state to avoid re-triggering
                }
            }
            is ResultState.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Error saving product: ${(saveResultState as ResultState.Error).message}",
                    withDismissAction = true
                )
                viewModel.resetSaveUpdateResult()
            }
            ResultState.Loading -> { /* UI shows progress indicator */ }
            else -> {} // Do nothing for initial/idle states
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Use viewModel.productId directly to determine Add/Edit title
                    Text(text = if (viewModel.productId.isNullOrBlank()) "Add New Product" else "Edit Product")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Show loading indicator when product is being fetched for editing
            if (productState is ResultState.Loading && !viewModel.productId.isNullOrBlank()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Input fields (no changes here, they use String states)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = price,
                onValueChange = { newValue ->
                    if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        price = newValue
                    }
                },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = rating,
                onValueChange = { newValue ->
                    if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        rating = newValue
                    }
                },
                label = { Text("Rating (e.g., 4.5)") }, // Changed label for clarity
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button Logic
            Button(
                onClick = {
                    val parsedPrice = price.toDoubleOrNull() ?: 0.0
                    val parsedRating = rating.toDoubleOrNull() ?: 0.0

                    if (viewModel.productId.isNullOrBlank()) {
                        // ADD NEW PRODUCT SCENARIO
                        val newProduct = Product(
                            // For new products, ID is typically 0 or null. Your ViewModel's saveProduct
                            // method already checks for product.id == 0 to determine add vs. update.
                            id = 0, // Use 0 as a placeholder for new products (backend will generate actual ID)
                            name = name,
                            description = description,
                            price = parsedPrice,
                            imageUrl = imageUrl,
                            category = category,
                            ratings = parsedRating
                        )
                        viewModel.saveProduct(newProduct) // Calls addProductUseCase internally
                    } else {
                        // EDIT EXISTING PRODUCT SCENARIO
                        val existingProductId = viewModel.productId?.toIntOrNull()
                        if (existingProductId != null) {
                            val updatedProduct = Product(
                                id = existingProductId, // Use the actual ID from navigation for update
                                name = name,
                                description = description,
                                price = parsedPrice,
                                imageUrl = imageUrl,
                                category = category,
                                ratings = parsedRating
                            )
                            viewModel.saveProduct(updatedProduct) // Calls updateProductUseCase internally
                        } else {
                            // This scenario should be rare if validation happens earlier,
                            // but good for robustness.
                            Log.e("AddEditProductScreen", "Attempted to update with invalid numeric productId from ViewModel: ${viewModel.productId}")
//                            snackbarHostState.showSnackbar(
//                                message = "Error: Invalid product ID for update operation.",
//                                withDismissAction = true,
//                                duration = SnackbarDuration.Long
//                            )
                        }
                    }
                },
                // Disable button if loading or required fields are empty/invalid
                enabled = saveResultState !is ResultState.Loading &&
                        name.isNotBlank() &&
                        price.isNotBlank() && price.toDoubleOrNull() != null && // Price must be a valid number
                        imageUrl.isNotBlank() &&
                        category.isNotBlank() &&
                        rating.isNotBlank() && rating.toDoubleOrNull() != null, // Rating must be a valid number
                modifier = Modifier.fillMaxWidth()
            ) {
                if (saveResultState is ResultState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = if (viewModel.productId.isNullOrBlank()) "Add Product" else "Update Product")
                }
            }
        }
    }
}