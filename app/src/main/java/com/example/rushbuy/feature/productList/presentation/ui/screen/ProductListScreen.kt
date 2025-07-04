package com.example.rushbuy.feature.productList.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.rushbuy.R
import com.example.rushbuy.feature.productList.presentation.ui.components.EmptyListMessage
import com.example.rushbuy.feature.productList.presentation.ui.components.ErrorFooter
import com.example.rushbuy.feature.productList.presentation.ui.components.LoadingFooter
import com.example.rushbuy.feature.productList.presentation.ui.components.ProductListItem
import com.example.rushbuy.feature.productList.presentation.ui.screen.SearchBar
import com.example.rushbuy.feature.productList.presentation.viewmodel.ProductListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavController,
) {
    val viewModel: ProductListViewModel = koinViewModel()
    //val searchQuery by viewModel.searchQuery.collectAsState(initial = "")
    val searchQuery: String by viewModel.searchQuery.collectAsState(initial = "")
    val products = viewModel.products.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.product_list_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged
            )

            when (products.loadState.refresh) {
                is LoadState.Loading -> {
                    LoadingFooter(modifier = Modifier.fillMaxSize())
                }

                is LoadState.Error -> {
                    val errorMessage =
                        (products.loadState.refresh as LoadState.Error).error.localizedMessage
                            ?: "Unknown error"
                    EmptyListMessage(
                        message = stringResource(R.string.initial_load_error) + "\n$errorMessage",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    if (products.itemCount == 0 && searchQuery.isNotBlank() && products.loadState.refresh is LoadState.NotLoading) {
                        EmptyListMessage(
                            message = stringResource(R.string.no_products_found),
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    } else if (products.itemCount == 0 && searchQuery.isBlank() && products.loadState.refresh is LoadState.NotLoading) {
                        EmptyListMessage(
                            message = stringResource(R.string.no_products_found),
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    } else {
                        LazyVerticalStaggeredGrid(
                            // CHANGE THIS LINE: from Adaptive to Fixed(2) for testing
                            columns = StaggeredGridCells.Fixed(2),
                            contentPadding = PaddingValues(all = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalItemSpacing = 8.dp,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = products.itemCount,
                                key = products.itemKey { it.id }
                            ) { index ->
                                val product = products[index]
                                if (product != null) {
                                    ProductListItem(
                                        product = product,
                                        onItemClick = { productId ->
                                            navController.navigate("product_detail/$productId")
                                            println("Clicked product with ID: $productId")
                                        },
                                        onAddToCartClick = { productToAdd ->
                                            println("Added ${productToAdd.name} to cart!")
                                        }
                                    )
                                }
                            }
                            when (products.loadState.append) {
                                is LoadState.Loading -> {
                                    item(span = StaggeredGridItemSpan.FullLine) {
                                        LoadingFooter(modifier = Modifier.fillMaxWidth())
                                    }
                                }

                                is LoadState.Error -> {
                                    item(span = StaggeredGridItemSpan.FullLine) {
                                        val errorMessage =
                                            (products.loadState.append as LoadState.Error).error.localizedMessage
                                                ?: "Unknown error"
                                        ErrorFooter(
                                            message = errorMessage,
                                            modifier = Modifier.fillMaxWidth()
                                        )
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
}