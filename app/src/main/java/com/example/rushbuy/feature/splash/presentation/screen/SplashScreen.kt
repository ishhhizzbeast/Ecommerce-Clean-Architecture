package com.example.rushbuy.feature.splash.presentation.screen

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rushbuy.core.theme.ManropeFontFamily
import com.example.rushbuy.core.theme.Orange200
import com.example.rushbuy.core.theme.Orange300
import com.example.rushbuy.core.theme.Orange400
import com.example.rushbuy.core.theme.Orange600
import com.example.rushbuy.core.theme.RushBuyTheme
import com.example.rushbuy.feature.splash.presentation.viewmodel.SplashNavigationEvent
import com.example.rushbuy.feature.splash.presentation.viewmodel.SplashUiState
import com.example.rushbuy.feature.splash.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel


@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onShowNoInternetToast: () -> Unit = {}
) {
    val viewModel: SplashViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    var logoVisible by remember { mutableStateOf(true) }
    var showScreen by remember { mutableStateOf(true) }

    // Smooth animation states
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(1500, easing = EaseInOutCubic),
        label = "logoAlpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.8f,
        animationSpec = tween(1500, easing = EaseInOutCubic),
        label = "logoScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "backgroundAnimation")
    val speedLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "speedLines"
    )

    // Start splash sequence when screen loads
    LaunchedEffect(Unit) {
        viewModel.startSplashSequence()
    }

    // Handle UI state changes
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            // When loading is done, start fade out animation
            delay(500) // Small delay to see the final state
            logoVisible = false

            delay(1500) // Wait for fade out animation to complete
            showScreen = false
        }
    }

    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is SplashNavigationEvent.NavigateToLogin -> {
                onNavigateToLogin()
                viewModel.onNavigationHandled()
            }
            is SplashNavigationEvent.NavigateToHome -> {
                if (!uiState.hasInternet) {
                    onShowNoInternetToast()
                }
                onNavigateToHome()
                viewModel.onNavigationHandled()
            }
            null -> {}
        }
    }

    if (showScreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // Use primary colors from the theme for the background gradient
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Enhanced Animated Background with Speed Lines
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Main speed lines - more visible
                for (i in 0..12) {
                    val y = (canvasHeight / 13) * i
                    val lineWidth = (80 + (i * 15)).dp.toPx()
                    val startX = canvasWidth + (speedLineOffset * canvasWidth * 1.5f) - (i * 120)
                    val endX = startX - lineWidth

                    // Primary speed lines with higher opacity, using theme colors
                    drawLine(
                        color = Orange300.copy(alpha = 0.4f),
                        start = Offset(startX, y),
                        end = Offset(endX, y),
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Secondary lines with glow effect, using theme colors
                    drawLine(
                        color = Orange600.copy(alpha = 0.3f),
                        start = Offset(startX + 30, y + 15),
                        end = Offset(endX + 30, y + 15),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Diagonal accent lines - more prominent, using theme colors
                for (i in 0..6) {
                    val startY = (canvasHeight / 7) * i
                    val lineLength = 150.dp.toPx()
                    val startX = canvasWidth + (speedLineOffset * canvasWidth * 1.3f) - (i * 100)

                    drawLine(
                        color = Orange400.copy(alpha = 0.6f),
                        start = Offset(startX, startY),
                        end = Offset(startX - lineLength, startY + 30),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Add some particle effects, using theme colors
                for (i in 0..20) {
                    val particleX = (speedLineOffset * canvasWidth * 2f - (i * 50)) % canvasWidth
                    val particleY = (canvasHeight / 21) * i
                    val radius = (2 + (i % 4)).dp.toPx()

                    drawCircle(
                        color = Orange200.copy(alpha = 0.4f),
                        radius = radius,
                        center = Offset(particleX, particleY)
                    )
                }
            }

            // RushBuy Logo with enhanced styling
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha)
            ) {
                // Main logo text with shadow effect
                Text(
                    text = "RushBuy",
                    // Use displayLarge from your custom typography
                    style = MaterialTheme.typography.displayLarge.copy(
                        // Keep ManropeFontFamily as it's directly from your typography definition
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold, // Override if you want even bolder than displayLarge default
                        fontSize = 52.sp, // You can keep this specific size if desired, or rely on displayLarge's default
                        letterSpacing = 3.sp,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    ),
                    color = MaterialTheme.colorScheme.onPrimary, // Use onPrimary for text color
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tagline with subtle styling
                Text(
                    text = "Shop Fast, Shop Smart",
                    // Use titleMedium from your custom typography
                    style = MaterialTheme.typography.titleMedium.copy(
                        // Keep ManropeFontFamily as it's directly from your typography definition
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Medium, // Override if needed
                        fontSize = 16.sp, // You can keep this specific size
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f), // Use onPrimary with slight transparency
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Loading indicator that shows loading state
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(120.dp)
                        .height(2.dp)
                        .alpha(if (uiState.isLoading) 1f else 0.3f),
                    color = MaterialTheme.colorScheme.onPrimary, // Use onPrimary for the progress indicator color
                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f) // Use onPrimary for track color
                )
            }
        }
    }
}

@Preview
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    RushBuyTheme {
        SplashScreen(
            onNavigateToLogin = {},
            onNavigateToHome = {},
            onShowNoInternetToast = {}
        )
    }
}