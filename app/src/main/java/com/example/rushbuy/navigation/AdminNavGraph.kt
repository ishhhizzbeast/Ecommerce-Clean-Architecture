package com.example.rushbuy.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.rushbuy.core.foundation.utils.Screen
import com.example.rushbuy.feature.admin.presentation.ui.component.AddEditProductScreen


// Changed to a NavGraphBuilder extension function
fun NavGraphBuilder.adminNavGraph(navController: NavController) { // This navController is the mainNavController from AppNavigation
    // The 'route' for this entire admin graph
    composable(Screen.AdminGraph.route) {
        AdminMainScreen(
            mainNavController = navController,     // This is the AppNavigation's NavController
            parentGraphNavController = navController // This NavController handles navigation to AddEditProductScreen (same as mainNavController in this setup)
        )
    }

    // AddEditProductScreen is still directly accessible from this top-level admin graph.
    // It is not part of the bottom navigation of AdminMainScreen.
    composable(
        route = Screen.AddEditProduct.routeWithArgs,
        arguments = Screen.AddEditProduct.arguments
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getString(Screen.AddEditProduct.PRODUCT_ID_ARG)
        AddEditProductScreen(
            navController = navController, // This screen uses the mainNavController
            productId = productId
        )
    }
    // Add other direct admin-specific screens here (e.g., AdminOrders management if not in bottom bar)
}