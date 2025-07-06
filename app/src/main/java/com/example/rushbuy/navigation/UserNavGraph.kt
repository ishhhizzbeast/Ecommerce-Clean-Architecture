package com.example.rushbuy.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rushbuy.core.foundation.utils.Screen
import com.example.rushbuy.feature.productList.presentation.ProductListScreen
import com.example.rushbuy.feature.productList.presentation.ui.components.ProductDetailScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import com.example.rushbuy.feature.Category.presentation.ui.CategoryScreen

// Data class for bottom navigation items (replaces BottomNavItem from previous MainScreen.kt)
sealed class UserBottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : UserBottomNavItem(Screen.UserHome.route, Icons.Default.Home, "Home") // Product List
    object Category : UserBottomNavItem(Screen.Category.route, Icons.Default.Category, "Category")
    object Cart : UserBottomNavItem(Screen.Cart.route, Icons.Default.ShoppingCart, "Cart")
    object Profile : UserBottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profile")
}


// This is the extension function to add the user graph to the main NavHost
fun NavGraphBuilder.userNavGraph(navController: NavController) {
    composable(Screen.UserGraph.route) {
        // UserMainScreen will contain its own NavHost for the bottom navigation tabs
        UserMainScreen(mainNavController = navController)
    }
}

@Composable
fun UserMainScreen(mainNavController: NavController) {
    // This navController manages the internal navigation within the UserMainScreen (bottom tabs)
    val userNavController = rememberNavController()



    // Using the Scaffold here to provide the bottom navigation bar
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by userNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Pass the list of bottom navigation items
                val bottomNavItems = listOf(
                    UserBottomNavItem.Home,
                    UserBottomNavItem.Category,
                    UserBottomNavItem.Cart,
                    UserBottomNavItem.Profile
                )
                bottomNavItems.forEach { screen ->
                    NavigationBarItem (
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            userNavController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(userNavController.graph.findStartDestination().route.toString()) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // This NavHost manages the content displayed based on the selected bottom tab
        NavHost(userNavController, startDestination = UserBottomNavItem.Home.route, Modifier.padding(innerPadding)) {
            composable(UserBottomNavItem.Home.route) { ProductListScreen(navController = userNavController) } // Your existing ProductListScreen
            composable(UserBottomNavItem.Category.route) { CategoryScreen(navController = userNavController) }
            // Use the actual CartScreen composable here
            composable(UserBottomNavItem.Cart.route) { com.example.rushbuy.feature.cart.presentation.ui.CartScreen(navController = userNavController) } // <-- UPDATED HERE
            composable(UserBottomNavItem.Profile.route) { ProfileScreen(navController = userNavController) }

            // Product Detail Screen (accessible from ProductListScreen, not a bottom nav item)
            composable(
                route = Screen.ProductDetail.routeWithArgs,
                arguments = Screen.ProductDetail.arguments
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt(Screen.ProductDetail.PRODUCT_ID_ARG)
                if (productId != null) {
                    ProductDetailScreen(
                        productId = productId,
                        navController = userNavController // Pass the internal userNavController
                    )
                } else {
                    androidx.compose.material3.Text("Error: Product ID not found.", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Profile Screen - Coming Soon!", style = MaterialTheme.typography.headlineMedium)
    }
}