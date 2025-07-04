package com.example.rushbuy.navigation

import RegisterScreen
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.rushbuy.core.foundation.utils.Screen
import com.example.rushbuy.feature.auth.domain.model.UserRole
import com.example.rushbuy.feature.auth.presentation.login.ui.LoginScreen
import com.example.rushbuy.feature.splash.presentation.screen.SplashScreen

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    navigation(
        startDestination = Screen.Splash.route,
        route = Screen.AuthGraph.route // The route for this entire nested graph
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.UserHome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onShowNoInternetToast = {
                    // Handle no internet toast if needed
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { userRole ->
                    Log.d("AuthNavGraph", "Login Success. Intended role: $userRole")
                    when (userRole) {
                        UserRole.USER -> {
                            navController.navigate(Screen.UserGraph.route) {
                                popUpTo(navController.graph.id) { inclusive = true } // Pop up the entire auth graph
                            }
                        }
                        UserRole.ADMIN -> {
                            navController.navigate(Screen.AdminGraph.route) {
                                popUpTo(navController.graph.id) { inclusive = true } // Pop up the entire auth graph
                            }
                        }
                        null -> {
                            Log.w("AuthNavGraph", "Login Success but UserRole was null. Defaulting to User home.")
                            navController.navigate(Screen.UserGraph.route) {
                                popUpTo(Screen.AuthGraph.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegistrationSuccess = {
                    // After registration, usually navigate to user home or login again
                    navController.navigate(Screen.UserGraph.route) {
                        popUpTo(Screen.AuthGraph.route) { inclusive = true }
                    }
                }
            )
        }
    }
}