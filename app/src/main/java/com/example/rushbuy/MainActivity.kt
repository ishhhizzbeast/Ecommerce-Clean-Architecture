package com.example.rushbuy

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.example.rushbuy.core.theme.RushBuyTheme
import com.example.rushbuy.feature.productList.presentation.ProductListScreen
import com.example.rushbuy.navigation.AppNavigation
import org.koin.androidx.compose.KoinAndroidContext


class MainActivity : ComponentActivity() {
    // Register the permission request launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted. You can proceed with showing notifications.
                // Log.d("Permissions", "POST_NOTIFICATIONS permission granted")
            } else {
                // Permission denied. Explain why notifications are useful, or disable features.
                // Log.w("Permissions", "POST_NOTIFICATIONS permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU is API 33
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        // Keep system splash very briefly, then transition to Compose
        enableEdgeToEdge()
        setContent {
            RushBuyTheme {
                val navController = rememberNavController()
                KoinAndroidContext {
                    AppNavigation(navController)

                }
            }
        }
    }
}