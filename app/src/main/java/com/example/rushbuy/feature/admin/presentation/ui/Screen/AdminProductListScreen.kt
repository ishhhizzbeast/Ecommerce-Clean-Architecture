// app/src/main/java/com.example.rushbuy.feature.admin.presentation.ui.Screen/AdminProductListScreen.kt
package com.example.rushbuy.feature.admin.presentation.ui.Screen

import com.example.rushbuy.feature.admin.presentation.viewmodel.AddEditProductViewModel
import com.example.rushbuy.feature.admin.presentation.viewmodel.ProductCrudEvent
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductListScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {

    val coroutineScope = rememberCoroutineScope()
    val adminHomeViewModel: AdminHomeViewModel = koinViewModel()
    val addEditProductViewModel: AddEditProductViewModel = koinViewModel() // <--- Get this ViewModel too

    val products = adminHomeViewModel.products.collectAsLazyPagingItems()
    val deleteResultState by adminHomeViewModel.deleteProductResult.collectAsState()

    // *** CRITICAL: Re-introduce the LaunchedEffect to listen for Add/Edit events ***
    LaunchedEffect(Unit) {
        addEditProductViewModel.productCrudEvent.collectLatest { event ->
            when (event) {
                ProductCrudEvent.ProductAdded, is ProductCrudEvent.ProductUpdated -> {
                    snackbarHostState.showSnackbar(
                        message = "Product saved successfully!",
                        duration = SnackbarDuration.Short
                    )
                    products.refresh() // <-- This refreshes the list!
                }
                is ProductCrudEvent.ProductDeleted -> {
                    // This event is already handled by deleteResultState, but if you want
                    // to handle it here explicitly for some reason, you could.
                    // However, products.refresh() is already called on deleteResultState success.
                }
            }
        }
    }


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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 0.dp, bottom = 0.dp, start = 0.dp, end = 0.dp) // Apply no extra padding, rely on parent paddingValues
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
                                        adminHomeViewModel.deleteProduct(productToDelete.id.toInt()) // Corrected ViewModel instance
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
        }
    }
}