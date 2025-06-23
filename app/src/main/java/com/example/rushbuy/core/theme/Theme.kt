package com.example.rushbuy.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,

    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,

    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,

    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,

    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,

    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    scrim = LightScrim,

    inverseSurface = Gray800,
    inverseOnSurface = Gray100,
    inversePrimary = Orange300,

    surfaceDim = Gray200,
    surfaceBright = Gray50,
    surfaceContainerLowest = White,
    surfaceContainerLow = Gray100,
    surfaceContainer = Gray200,
    surfaceContainerHigh = Gray300,
    surfaceContainerHighest = Gray400
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,

    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,

    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,

    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,

    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,

    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    scrim = DarkScrim,

    inverseSurface = Gray100,
    inverseOnSurface = Gray800,
    inversePrimary = Orange600,

    surfaceDim = Gray900,
    surfaceBright = Gray700,
    surfaceContainerLowest = Black,
    surfaceContainerLow = Gray800,
    surfaceContainer = Gray700,
    surfaceContainerHigh = Gray600,
    surfaceContainerHighest = Gray500
)

@Composable
fun RushBuyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RushBuyTypography,
        content = content
    )
}
// Extension functions for custom ecommerce colors
@Composable
fun MaterialTheme.ecommerceColors(): EcommerceColors {
    return if (isSystemInDarkTheme()) {
        EcommerceColors(
            discount = Red300,
            sale = Green300,
            price = Orange300,
            outOfStock = Gray500,
            newItem = Blue300,
            favorite = Color(0xFFFF4081),
            cart = Orange300,
            checkout = Green400,
            rating = Amber300
        )
    } else {
        EcommerceColors(
            discount = DiscountRed,
            sale = SaleGreen,
            price = PriceOrange,
            outOfStock = OutOfStockGray,
            newItem = NewItemBlue,
            favorite = FavoriteRed,
            cart = CartOrange,
            checkout = CheckoutGreen,
            rating = RatingYellow
        )
    }
}

data class EcommerceColors(
    val discount: androidx.compose.ui.graphics.Color,
    val sale: androidx.compose.ui.graphics.Color,
    val price: androidx.compose.ui.graphics.Color,
    val outOfStock: androidx.compose.ui.graphics.Color,
    val newItem: androidx.compose.ui.graphics.Color,
    val favorite: androidx.compose.ui.graphics.Color,
    val cart: androidx.compose.ui.graphics.Color,
    val checkout: androidx.compose.ui.graphics.Color,
    val rating: androidx.compose.ui.graphics.Color
)