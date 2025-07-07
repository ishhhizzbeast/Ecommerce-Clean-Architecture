package com.example.rushbuy.feature.admin.presentation.ui.component

import com.example.rushbuy.feature.admin.presentation.viewmodel.AddEditProductViewModel
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
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavController,
    productId: String?
) {
    val viewModel: AddEditProductViewModel = koinViewModel()

    val productState by viewModel.productToEdit.collectAsState()
    val saveResultState by viewModel.saveUpdateResult.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var rating by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    // --- LaunchedEffect to handle initial product loading / form resetting ---
    // This effect ensures the UI fields reflect the loaded product for editing.
    // It also resets fields for a new product.
    LaunchedEffect(productState) {
        when (productState) {
            is ResultState.Success -> {
                val product = (productState as ResultState.Success).data
                product?.let {
                    name = it.name
                    description = it.description
                    price = it.price.toString()
                    imageUrl = it.imageUrl
                    category = it.category
                    rating = it.ratings.toString()
                } ?: run {
                    // If product is null (new product or not found), reset fields
                    name = ""
                    description = ""
                    price = ""
                    imageUrl = ""
                    category = ""
                    rating = ""
                }
            }
            is ResultState.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Failed to load product: ${(productState as ResultState.Error).message}",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long // Show for longer if it's an error
                )
                // Optionally navigate back if product loading failed critically for edit mode
                // navController.popBackStack()
            }
            ResultState.Loading -> { /* Handled by LinearProgressIndicator below */ }
            else -> {}
        }
    }

    // Effect for handling saveResultState (navigation on success, snackbar on error)
    LaunchedEffect(saveResultState) {
        when (saveResultState) {
            is ResultState.Success -> {
                // MODIFIED: This is the key change.
                // We now check if the data is specifically 'Unit' (meaning a save operation completed)
                // AND that the current state is not the initial ResultState.Success(Unit)
                // A common pattern is to only react if the state was previously Loading.
                // However, the simplest fix is to just react to Success(Unit) and then reset.
                // The `LaunchedEffect` will only re-run if `saveResultState` *changes*.
                // So, if it changes from Loading to Success(Unit), this block will trigger.
                snackbarHostState.showSnackbar(
                    message = "Product saved successfully!",
                    duration = SnackbarDuration.Short
                )
                navController.popBackStack() // <--- This will now execute!
                viewModel.resetSaveUpdateResult() // Reset ViewModel state to avoid re-triggering
            }
            is ResultState.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Error saving product: ${(saveResultState as ResultState.Error).message}",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long // Show for longer if it's an error
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
            if (productState is ResultState.Loading && !viewModel.productId.isNullOrBlank()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

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
                label = { Text("Rating (e.g., 4.5)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val parsedPrice = price.toDoubleOrNull() ?: 0.0
                    val parsedRating = rating.toDoubleOrNull() ?: 0.0

                    if (viewModel.productId.isNullOrBlank()) {
                        val newProduct = Product(
                            id = 0,
                            name = name,
                            description = description,
                            price = parsedPrice,
                            imageUrl = imageUrl,
                            category = category,
                            ratings = parsedRating
                        )
                        viewModel.saveProduct(newProduct)
                    } else {
                        val existingProductId = viewModel.productId?.toIntOrNull()
                        if (existingProductId != null) {
                            val updatedProduct = Product(
                                id = existingProductId,
                                name = name,
                                description = description,
                                price = parsedPrice,
                                imageUrl = imageUrl,
                                category = category,
                                ratings = parsedRating
                            )
                            viewModel.saveProduct(updatedProduct)
                        } else {
                            Log.e("AddEditProductScreen", "Attempted to update with invalid numeric productId from ViewModel: ${viewModel.productId}")
                        }
                    }
                },
                enabled = saveResultState !is ResultState.Loading &&
                        name.isNotBlank() &&
                        price.isNotBlank() && price.toDoubleOrNull() != null &&
                        imageUrl.isNotBlank() &&
                        category.isNotBlank() &&
                        rating.isNotBlank() && rating.toDoubleOrNull() != null,
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