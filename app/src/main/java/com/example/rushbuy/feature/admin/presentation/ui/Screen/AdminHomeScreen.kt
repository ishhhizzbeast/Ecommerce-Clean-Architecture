package com.example.rushbuy.feature.admin.presentation.ui.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.feature.admin.presentation.ui.component.ProductAdminCard
import com.example.rushbuy.feature.admin.presentation.viewmodel.AdminHomeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.rushbuy.core.foundation.utils.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController, // NavController passed from the NavHost
) {
    val viewModel: AdminHomeViewModel = koinViewModel() // Koin injects AdminHomeViewModel
    // Collect states from the ViewModel
    val productsState by viewModel.products.collectAsState()
    val deleteResultState by viewModel.deleteProductResult.collectAsState()

    // Snackbar host state for showing messages (e.g., deletion feedback)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope() // For launching snackbar coroutines

    // Effect to observe the deleteResultState and show appropriate feedback
    LaunchedEffect(deleteResultState) {
        when (deleteResultState) {
            is ResultState.Success<*> -> {
                // Only show a message if it's a new success (not the initial Unit state)
                if (deleteResultState != ResultState.Success(Unit)) {
                    snackbarHostState.showSnackbar(
                        message = "Product deleted successfully!",
                        duration = SnackbarDuration.Short
                    )
                    // If you want to explicitly clear the deleteResultState in ViewModel
                    // to prevent re-showing messages on recomposition, add a reset function to ViewModel:
                    // viewModel.resetDeleteResultState()
                }
            }
            is ResultState.Error -> {
                val errorMessage = (deleteResultState as ResultState.Error).message
                snackbarHostState.showSnackbar(
                    message = "Error deleting product: $errorMessage",
                    withDismissAction = true // Allow user to dismiss
                )
            }
            else -> {} // Do nothing for ResultState.Loading or initial ResultState.Success(Unit)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Admin Dashboard") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Navigate to the AddEditProductScreen for adding a new product.
                // We pass null for productId, indicating a new product.
                navController.navigate(Screen.AddEditProduct.createRoute(null))
            }) {
                Icon(Icons.Filled.Add, "Add new product")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) } // Attach the SnackbarHost
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply Scaffold's padding
        ) {
            when (productsState) {
                is ResultState.Loading -> {
                    // Show a fullscreen loading indicator when products are being fetched
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ResultState.Success -> {
                    val products = (productsState as ResultState.Success).data
                    if (products.isEmpty()) {
                        // Show a message if no products are found
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No products found. Click '+' to add one.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        // Display the list of products using LazyColumn
                        LazyColumn(
                            contentPadding = PaddingValues(all = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp) // Space between cards
                        ) {
                            items(products, key = { it.id }) { product ->
                                ProductAdminCard(
                                    product = product,
                                    onEditClick = { productToEdit ->
                                        // Navigate to AddEditProductScreen for editing.
                                        // Pass the product's ID to the navigation route.
                                        navController.navigate(Screen.AddEditProduct.createRoute(productToEdit.id.toString()))
                                    },
                                    onDeleteClick = { productToDelete ->
                                        // Call ViewModel function to initiate product deletion
                                        // Ensure productToDelete.id can be safely converted to Int if your UseCase expects Int
                                        viewModel.deleteProduct(productToDelete.id.toInt())
                                    }
                                )
                            }
                        }
                    }
                }
                is ResultState.Error -> {
                    val errorMessage = (productsState as ResultState.Error).message
                    // Show an error message with a retry option
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Error: $errorMessage",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.getProducts() }) { // Retry button
                                Text("Retry")
                            }
                        }
                    }
                }

                ResultState.Idle -> TODO()
            }
        }
    }
}

// --- Preview ---
//@Preview(showBackground = true)
//@Composable
//fun PreviewAdminHomeScreen() {
//    // For previews, you'd typically mock the ViewModel or provide a dummy NavController.
//    // This preview just shows the structure.
//    MaterialTheme {
//        AdminHomeScreen(navController = rememberNavController())
//    }
//}