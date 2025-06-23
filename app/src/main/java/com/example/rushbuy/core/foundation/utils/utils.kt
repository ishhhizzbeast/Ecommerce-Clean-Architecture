package com.example.rushbuy.core.foundation.utils

import android.R.attr.type
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()

    data class Success<out T>(val data: T) : ResultState<T>()

    data class Error(val message: String) : ResultState<Nothing>()

    object Idle : ResultState<Nothing>() // Added Idle state for better UI management
}

sealed class Screen(val route: String) {


    // These define the routes for the *nested navigation graphs themselves*
    // or direct top-level screens.
    object AuthGraph : Screen("auth_graph_route")
    object UserGraph : Screen("user_graph_route")
    object AdminGraph : Screen("admin_graph_route") // This will be the entry for AdminSectionNavGraph

    // --- Individual Screens within AuthGraph ---
    object Splash : Screen("splash_screen_route")
    object Login : Screen("login_screen_route")
    object Register : Screen("register_screen_route")

    // --- Individual Screen within UserGraph (for now) ---
    object UserHome : Screen("user_home_screen_route")

    // --- Individual Screens within AdminGraph (these routes are internal to AdminGraph's NavHost) ---
    // The start destination for the AdminGraph's internal NavHost
    object AdminHome : Screen("admin_home_screen_route")

    // Route for adding or editing a product within the AdminGraph
    object AddEditProduct : Screen("add_edit_product") {
        const val PRODUCT_ID_ARG = "productId" // Constant for the argument key

        // Base route pattern with optional argument placeholder
        val routeWithArgs = "${route}?$PRODUCT_ID_ARG={$PRODUCT_ID_ARG}"

        // NavArguments definition for this route (used by the composable in the NavHost)
        val arguments: List<NamedNavArgument> = listOf(
            navArgument(PRODUCT_ID_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )

        /**
         * Helper to create the navigation route for AddEditProductScreen.
         * Pass null or an empty string for adding a new product.
         * Pass a valid product ID string for editing an existing product.
         */
        fun createRoute(productId: String? = null): String {
            return if (productId.isNullOrEmpty()) {
                route // For adding, use base route without query param
            } else {
                "${route}?$PRODUCT_ID_ARG=${productId}" // For editing, include productId
            }
        }
    }

    // Add other screens as needed, maintaining this pattern
    // object ProductDetail : Screen("product_detail_screen_route") { /* ... */ }
    // object AdminOrders : Screen("admin_orders_screen_route")
}