package com.example.rushbuy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rushbuy.core.foundation.utils.Screen
import com.example.rushbuy.feature.admin.presentation.ui.Screen.AdminHomeScreen
import com.example.rushbuy.feature.admin.presentation.ui.component.AddEditProductScreen


@Composable
fun AdminNavGraph() { // This is a standalone Composable, not a NavGraphBuilder extension
    val navController = rememberNavController() // This NavHost has its own NavController

    NavHost(
        navController = navController,
        startDestination = Screen.AdminHome.route // AdminHome is the start for this internal graph
    ) {
        composable(Screen.AdminHome.route) {
            AdminHomeScreen(
                navController = navController // Pass its own navController to AdminHomeScreen
            )
        }

        composable(
            route = Screen.AddEditProduct.routeWithArgs,
            arguments = Screen.AddEditProduct.arguments // Reusing arguments defined in Screen object
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(Screen.AddEditProduct.PRODUCT_ID_ARG)
            AddEditProductScreen(
                navController = navController, // Pass its own navController to AddEditProductScreen
                productId = productId
            )
        }
        // Add other admin-specific screens here (e.g., AdminOrders, AdminUsers)
        // composable(Screen.AdminOrders.route) { AdminOrdersScreen(navController) }
    }
}