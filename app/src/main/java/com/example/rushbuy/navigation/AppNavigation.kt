package com.example.rushbuy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rushbuy.core.foundation.utils.Screen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController() // The single main NavController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AuthGraph.route // Start with the authentication graph
    ) {
        // Include the authentication navigation graph
        authNavGraph(navController = navController)

        // Include the user section navigation graph
        userNavGraph(navController = navController)

        // The Admin Section's entry point: hosts its own NavHost
        adminNavGraph(navController)
    }
}