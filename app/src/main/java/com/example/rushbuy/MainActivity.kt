package com.example.rushbuy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.rushbuy.core.theme.RushBuyTheme
import com.example.rushbuy.navigation.AppNavigation


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Keep system splash very briefly, then transition to Compose
        enableEdgeToEdge()
        setContent {
            RushBuyTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}
