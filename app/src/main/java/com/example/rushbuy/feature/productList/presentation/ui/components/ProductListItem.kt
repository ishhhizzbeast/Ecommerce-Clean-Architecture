package com.example.rushbuy.feature.productList.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star // For rating icon
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rushbuy.core.foundation.domain.model.Product
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductListItem(
    product: Product,
    onItemClick: (Int) -> Unit,
    onAddToCartClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(product.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = product.imageUrl, // This comes from the 'images' list via mapping
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    // !!! KEY CHANGE FOR STAGGERING !!!
                    // REMOVE THE FIXED HEIGHT. This allows the image's intrinsic aspect ratio
                    // (if images in 'images' array vary in aspect ratio) to determine height.
                    // This is more likely to create height differences than 'thumbnail'.
                    // .height(150.dp) // <--- COMMENT OUT OR REMOVE THIS LINE
                    .clip(MaterialTheme.shapes.medium)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f", product.ratings),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
//                Spacer(modifier = Modifier.height(4.dp))
                // !!! ANOTHER KEY CHANGE FOR STAGGERING !!!
                // Ensure product descriptions are actually different lengths in your data.
//             Text(
//                    text = product.description,
//                    style = MaterialTheme.typography.bodySmall,
//                    maxLines = 3, // Allow up to 2 lines; if some are 1 line, others 2, it varies height
//                    overflow = TextOverflow.Ellipsis
//        )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatPrice(product.price),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onAddToCartClick(product) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Add to Cart")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(price)
}

@Preview(showBackground = true)
@Composable
fun ProductListItemPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ProductListItem(
                product = Product(
                    id = 1,
                    name = "Short Item",
                    description = "This is a short description.",
                    price = 10.0,
                    imageUrl = "https://cdn.dummyjson.com/product-images/1/1.jpg", // Example: A wider image
                    category = "Test",
                    ratings = 4.0
                ),
                onItemClick = {}, onAddToCartClick = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProductListItem(
                product = Product(
                    id = 2,
                    name = "Longer Item Name That Might Wrap",
                    description = "This is a much longer description that will hopefully span two lines, thus increasing the height of this card significantly compared to the short description above.",
                    price = 20.0,
                    imageUrl = "https://cdn.dummyjson.com/product-images/2/2.jpg", // Example: A taller image
                    category = "Test",
                    ratings = 4.5
                ),
                onItemClick = {}, onAddToCartClick = {}
            )
        }
    }
}