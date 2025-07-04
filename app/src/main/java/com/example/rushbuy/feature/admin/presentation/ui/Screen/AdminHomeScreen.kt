// app/src/main/java/com.example.rushbuy.feature.admin.presentation.ui.Screen/AdminHomeScreen.kt
package com.example.rushbuy.feature.admin.presentation.ui.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.core.foundation.utils.Screen
import com.example.rushbuy.feature.admin.presentation.ui.component.ProductAdminCard
import com.example.rushbuy.feature.admin.presentation.viewmodel.AdminHomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
) {
    val viewModel: AdminHomeViewModel = koinViewModel()

    // CHANGED: Collect PagingData as LazyPagingItems
    val products = viewModel.products.collectAsLazyPagingItems()

    // Keep deleteResultState as it's still a single operation result
    val deleteResultState by viewModel.deleteProductResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Effect to observe the deleteResultState and show appropriate feedback
    LaunchedEffect(deleteResultState) {
        when (deleteResultState) {
            is ResultState.Success<*> -> {
                // Check if the success is due to an actual deletion, not just initial Unit state
                if (deleteResultState != ResultState.Success(Unit)) {
                    snackbarHostState.showSnackbar(
                        message = "Product deleted successfully!",
                        duration = SnackbarDuration.Short
                    )
                    // CRITICAL: Trigger refresh of the PagingData after successful deletion
                    products.refresh() // <--- This will reload the list
                    // Optionally, reset the deleteResultState in ViewModel if you only want the message to show once
                    // viewModel.resetDeleteResultState() // You'd need to add this function to your ViewModel
                }
            }
            is ResultState.Error -> {
                val errorMessage = (deleteResultState as ResultState.Error).message
                snackbarHostState.showSnackbar(
                    message = "Error deleting product: $errorMessage",
                    withDismissAction = true
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
                navController.navigate(Screen.AddEditProduct.createRoute(null))
            }) {
                Icon(Icons.Filled.Add, "Add new product")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // CHANGED: Handle PagingData load states
            when (products.loadState.refresh) { // Initial load or full refresh
                is LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is LoadState.Error -> {
                    val errorMessage = (products.loadState.refresh as LoadState.Error).error.localizedMessage ?: "Unknown error"
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Error loading products: $errorMessage",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { products.retry() }) { // Retry button for refresh
                                Text("Retry")
                            }
                        }
                    }
                }
                is LoadState.NotLoading -> {
                    if (products.itemCount == 0) {
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
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Use items from LazyPagingItems
                            items(
                                count = products.itemCount,
                                key = products.itemKey { it.id } // Provide a key for better performance
                            ) { index ->
                                val product = products[index]
                                if (product != null) {
                                    ProductAdminCard(
                                        product = product,
                                        onEditClick = { productToEdit ->
                                            navController.navigate(Screen.AddEditProduct.createRoute(productToEdit.id.toString()))
                                        },
                                        onDeleteClick = { productToDelete ->
                                            viewModel.deleteProduct(productToDelete.id.toInt())
                                        }
                                    )
                                }
                            }

                            // Handle load state for appending new pages
                            when (products.loadState.append) {
                                is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                                is LoadState.Error -> {
                                    val errorMessage = (products.loadState.append as LoadState.Error).error.localizedMessage ?: "Unknown error"
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Error loading more: $errorMessage",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            Button(onClick = { products.retry() }) {
                                                Text("Retry")
                                            }
                                        }
                                    }
                                }
                                else -> { /* Do nothing */ }
                            }
                        }
                    }
                }
                // ResultState.Idle is not applicable here anymore for products
                // As PagingData doesn't have an 'Idle' state like your custom ResultState
            }
        }
    }
}

// --- Preview ---
// Remove the @Preview if it causes issues, or create a simplified mock ViewModel for it
/*
@Preview(showBackground = true)
@Composable
fun PreviewAdminHomeScreen() {
    MaterialTheme {
        AdminHomeScreen(navController = rememberNavController())
    }
}
*/