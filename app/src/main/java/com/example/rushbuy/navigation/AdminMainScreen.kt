package com.example.rushbuy.navigation

import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rushbuy.core.foundation.utils.AdminBottomNavItem
import com.example.rushbuy.feature.profile.presentation.ui.ProfileScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rushbuy.core.foundation.utils.Screen
import com.example.rushbuy.feature.admin.presentation.ui.Screen.AdminProductListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    mainNavController: NavController,       // The AppNavigation's NavController (for logout)
    parentGraphNavController: NavController // This is also the AppNavigation's NavController (for AddEditProduct)
) {
    // This navController manages the internal bottom navigation tabs (Products, Profile)
    val adminBottomNavController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Admin Dashboard") })
        },
        floatingActionButton = {
            val navBackStackEntry by adminBottomNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute == AdminBottomNavItem.Products.route) {
                FloatingActionButton(onClick = {
                    // Use the parentGraphNavController (which is mainNavController) to navigate
                    parentGraphNavController.navigate(Screen.AddEditProduct.createRoute(null))
                }) {
                    Icon(Icons.Filled.Add, "Add new product")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by adminBottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val bottomNavItems = listOf(
                    AdminBottomNavItem.Products,
                    AdminBottomNavItem.Profile
                )
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            adminBottomNavController.navigate(screen.route) {
                                popUpTo(adminBottomNavController.graph.findStartDestination().route.toString()) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = adminBottomNavController,
            startDestination = AdminBottomNavItem.Products.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Products List Screen for Admin
            composable(AdminBottomNavItem.Products.route) {
                AdminProductListScreen(
                    navController = parentGraphNavController, // Pass parentGraphNavController for AddEditProduct
                    snackbarHostState = snackbarHostState
                )
            }

            // Profile Screen for Admin (reusing the common ProfileScreen)
            composable(AdminBottomNavItem.Profile.route) {
                ProfileScreen(
                    internalNavController = adminBottomNavController, // For internal profile navigation if needed
                    mainNavController = mainNavController           // Crucial for logout, navigates outside AdminGraph
                )
            }
        }
    }
}