package com.example.rushbuy.feature.Category.presentation.ui

// import androidx.hilt.navigation.compose.hiltViewModel // REMOVE THIS LINE

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells

import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
//import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.core.foundation.utils.Screen
import com.example.rushbuy.feature.Category.presentation.viewmodel.CategoryViewModel
import com.example.rushbuy.feature.cart.presentation.viewmodel.CartViewModel
import com.example.rushbuy.feature.productList.presentation.ui.components.ProductListItem
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel() // Inject CartViewModel
) {
    val allCategoriesResult by viewModel.allAvailableCategories.collectAsState()
    val selectedCategory by viewModel.currentSelectedCategory.collectAsState()
    val products = viewModel.products.collectAsLazyPagingItems()

    val context = LocalContext.current // Get the current context for Toast

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse Products", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // ... (Category Filter Chips Row - no changes here)
            when (allCategoriesResult) {
                is ResultState.Loading -> {
                    Text("Loading categories...", modifier = Modifier.padding(vertical = 8.dp))
                }
                is ResultState.Success -> {
                    val categories = (allCategoriesResult as ResultState.Success).data
                    if (categories.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            item {
                                SuggestionChip(
                                    onClick = { viewModel.clearCategoryFilter() },
                                    label = { Text("All Products") },
                                    enabled = selectedCategory.isNotBlank()
                                )
                            }
                            items(categories) { category ->
                                SuggestionChip(
                                    onClick = { viewModel.onCategorySelected(category.name) },
                                    label = { Text(category.name) },
                                    enabled = selectedCategory != category.name
                                )
                            }
                        }
                    } else {
                        Text("No categories found.", modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                is ResultState.Error -> {
                    val errorMessage = (allCategoriesResult as ResultState.Error).message
                    Text("Error loading categories: $errorMessage", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
                }
                ResultState.Idle -> { /* Do nothing, waiting for initial load */ }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product List (NOW USING LazyVerticalStaggeredGrid)
            if (products.itemCount == 0 && products.loadState.refresh is androidx.paging.LoadState.Loading) {
                Text("Loading products...", modifier = Modifier.padding(vertical = 8.dp))
            } else if (products.itemCount == 0 && products.loadState.refresh is androidx.paging.LoadState.Error) {
                val error = products.loadState.refresh as? androidx.paging.LoadState.Error
                Text("Error loading products: ${error?.error?.localizedMessage ?: "Unknown error"}", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            } else if (products.itemCount == 0 && products.loadState.append.endOfPaginationReached && selectedCategory.isNotBlank()) {
                Text("No products found for '${selectedCategory}'.", modifier = Modifier.padding(vertical = 8.dp))
            } else if (products.itemCount == 0 && products.loadState.append.endOfPaginationReached && selectedCategory.isBlank()) {
                Text("No products available.", modifier = Modifier.padding(vertical = 8.dp))
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(all = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        count = products.itemCount,
                        key = products.itemKey { product ->
                            product.let { "${it.id}-${UUID.randomUUID()}" }
                        }
                    ) { index ->
                        val product = products[index]
                        if (product != null) {
                            ProductListItem(
                                product = product,
                                onItemClick = { productId ->
                                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                                    println("Clicked product with ID: $productId")
                                },
                                onAddToCartClick = { productToAdd ->
                                    // CALL THE CART VIEWMODEL HERE
                                    cartViewModel.onAddToCart(
                                        productId = productToAdd.id.toString(),
                                        name = productToAdd.name,
                                        imageUrl = productToAdd.imageUrl,
                                        price = productToAdd.price
                                    )
                                    Toast.makeText(context, "${productToAdd.name} added to cart!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                    // ... (loading/error footers - no changes here)
                    when (products.loadState.append) {
                        is androidx.paging.LoadState.Loading -> {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Text("Loading more products...", modifier = Modifier.fillMaxWidth().padding(8.dp))
                            }
                        }
                        is androidx.paging.LoadState.Error -> {
                            val error = products.loadState.append as androidx.paging.LoadState.Error
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Text("Error loading more: ${error.error.localizedMessage}", color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth().padding(8.dp))
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}