package com.example.rushbuy.feature.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rushbuy.core.foundation.utils.Screen // Make sure this import is correct
import com.example.rushbuy.feature.profile.presentation.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel // Koin extension for Compose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    internalNavController: NavController,
    mainNavController: NavController
) {
    val viewModel: UserProfileViewModel = koinViewModel() // Inject ViewModel using Koin
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // --- Side Effect: Handle Navigation after successful logout ---
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            // Navigate to the login screen
            mainNavController.navigate(Screen.Login.route) {
                // Clear the back stack to prevent going back to authenticated screens
                popUpTo(internalNavController.graph.id) {
                    inclusive = true // Pop the root of the graph too
                }
            }
            // Reset the logout state in the ViewModel to prevent re-navigation on recomposition
            viewModel.logoutHandled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply Scaffold's padding
                .padding(horizontal = 16.dp, vertical = 24.dp), // Add screen-specific padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Align content to top initially
        ) {
            // --- Always display the default user icon ---
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(24.dp)) // Spacer after the icon

            // --- Conditional UI based on ViewModel's UI State ---
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Loading profile...", style = MaterialTheme.typography.bodyLarge)
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchUserProfile() }) {
                        Text("Retry")
                    }
                }
                uiState.userProfile != null -> {
                    // Display user profile information
                    Text(
                        text = uiState.userProfile!!.displayName ?: "No Name Provided",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = uiState.userProfile!!.email ?: "No Email Provided",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Logout", color = MaterialTheme.colorScheme.onError)
                    }
                }
                // Fallback for cases where no profile is found and no error (e.g., initially null or session expired)
                else -> {
                    Text(
                        text = "No user profile found. Please log in.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { internalNavController.navigate(Screen.Login.route) }) {
                        Text("Go to Login")
                    }
                }
            }
        }
    }
}