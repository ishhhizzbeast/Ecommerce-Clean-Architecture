package com.example.rushbuy.core.foundation.utils

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    object AdminGraph : Screen("admin_graph_route")

    // --- Individual Screens within AuthGraph ---
    object Splash : Screen("splash_screen_route")
    object Login : Screen("login_screen_route")
    object Register : Screen("register_screen_route")

    // --- Individual Screens within UserGraph (Bottom Navigation Items) ---
    // User Home is essentially the Product List
    object UserHome : Screen("user_home_route") // This will be ProductListScreen
    object Category : Screen("category_route")
    object Cart : Screen("cart_route")
    object Profile : Screen("profile_route")

    // --- Individual Screens that are part of the UserGraph but NOT in Bottom Navigation ---
    // Product Detail Screen
    object ProductDetail : Screen("product_detail") {
        const val PRODUCT_ID_ARG = "productId"
        val routeWithArgs = "${route}/{${PRODUCT_ID_ARG}}" // Path argument

        val arguments: List<NamedNavArgument> = listOf(
            navArgument(PRODUCT_ID_ARG) { type = NavType.IntType }
        )

        fun createRoute(productId: Int): String {
            return "${route}/${productId}"
        }
    }


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
}


@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    minimizedMaxLines: Int = 3, // Initial lines to show when collapsed
    style: TextStyle = LocalTextStyle.current,
    color: Color = LocalContentColor.current,
    readMoreText: String = "... Read more",
    readLessText: String = " Read less",
    readMoreColor: Color = Color.Blue, // Or MaterialTheme.colorScheme.primary
    readMoreFontWeight: FontWeight = FontWeight.Bold
) {
    var expanded by remember { mutableStateOf(false) } // State to control expansion
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    val textLayoutResult = textLayoutResultState.value

    // Derived state to determine if "Read more/less" button should be shown.
    // This is true if the text actually overflows the minimizedMaxLines.
    val canTextExpand by remember(textLayoutResult, minimizedMaxLines) {
        derivedStateOf {
            if (textLayoutResult == null) false
            else textLayoutResult.didOverflowHeight || textLayoutResult.lineCount > minimizedMaxLines
        }
    }

    Text(
        text = buildAnnotatedString {
            if (expanded || !canTextExpand) {
                // If expanded or if text doesn't need to expand, show full text
                append(text)
                if (canTextExpand && expanded) { // Show "Read less" only if it was initially truncated and is now expanded
                    pushStyle(SpanStyle(color = readMoreColor, fontWeight = readMoreFontWeight))
                    append(readLessText)
                    pop()
                }
            } else {
                // When collapsed and text can expand, show truncated text with "Read more"
                // Find the index to truncate for the ellipsis
                val lastCharIndex = if (textLayoutResult != null && minimizedMaxLines > 0 && minimizedMaxLines <= textLayoutResult.lineCount) {
                    textLayoutResult.getLineEnd(minimizedMaxLines - 1)
                } else {
                    // Fallback for cases where minimizedMaxLines is 0 or greater than total lines
                    text.length
                }

                // Append the truncated text, ensure it doesn't end with whitespace before "..."
                append(text.substring(0, lastCharIndex).trimEnd())

                pushStyle(SpanStyle(color = readMoreColor, fontWeight = readMoreFontWeight))
                append(readMoreText)
                pop()
            }
        },
        modifier = modifier
            .animateContentSize(animationSpec = tween(durationMillis = 100))
            .clickable(enabled = canTextExpand) { // Only clickable if text can actually expand
                expanded = !expanded // Toggle the expanded state
            },
        // MaxLines and Overflow for the Text composable itself.
        // When not expanded and text can expand, apply the minimizedMaxLines limit.
        // Otherwise, allow it to show full content or clip naturally.
        maxLines = if (expanded || !canTextExpand) Int.MAX_VALUE else minimizedMaxLines,
        // TextOverflow.Clip is important when we're manually adding "..."
        overflow = TextOverflow.Clip,
        style = style,
        color = color,
        // onTextLayout provides the TextLayoutResult needed to determine truncation
        onTextLayout = { textLayoutResultState.value = it }
    )
}
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home_route", Icons.Default.Home, "Home")
    object Category : BottomNavItem("category_route", Icons.Default.Category, "Category")
    object Cart : BottomNavItem("cart_route", Icons.Default.ShoppingCart, "Cart")
    object Profile : BottomNavItem("profile_route", Icons.Default.Person, "Profile")
}