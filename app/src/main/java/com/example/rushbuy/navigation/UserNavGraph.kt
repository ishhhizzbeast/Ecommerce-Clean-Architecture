package com.example.rushbuy.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.rushbuy.core.foundation.utils.Screen
import androidx.navigation.compose.navigation

fun NavGraphBuilder.userNavGraph(navController: NavController) {
    navigation(
        startDestination = Screen.UserHome.route,
        route = Screen.UserGraph.route // The route for this entire nested graph
    ) {
        composable(Screen.UserHome.route) {
            // Replace with your actual User Home Screen
            DummyUserHomeScreen()
        }
        // Add other user screens here
        // composable(Screen.ProductDetail.route) { ProductDetailScreen(navController) }
    }
}

// Keep your DummyUserHomeScreen here for now, or move it to a proper user.presentation package
@Composable
fun DummyUserHomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🏠",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to User Home Screen",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This is a dummy User home screen.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}