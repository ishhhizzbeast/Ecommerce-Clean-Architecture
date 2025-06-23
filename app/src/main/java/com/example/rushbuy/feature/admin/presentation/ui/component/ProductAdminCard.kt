package com.example.rushbuy.feature.admin.presentation.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@OptIn(ExperimentalMaterial3Api::class) // For Card in Material 3
@Composable
fun ProductAdminCard(
    product: Product,
    onEditClick: (Product) -> Unit, // Callback for edit button click
    onDeleteClick: (Product) -> Unit // Callback for delete button click
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = product.imageUrl, // URL of the product image
                contentDescription = "${product.name} image",
                contentScale = ContentScale.Crop, // Crop to fill the bounds
                modifier = Modifier
                    .size(80.dp) // Fixed size for the image
                    .clip(RoundedCornerShape(4.dp)) // Slightly rounded corners for the image
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Product Details (Name and Price)
            Column(
                modifier = Modifier.weight(1f) // Takes up remaining space
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${product.price}", // Format price as currency
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Spacer to push icons to the right if needed, though weight(1f) already does this
            Spacer(modifier = Modifier.width(8.dp))

            // Edit and Delete Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onEditClick(product) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${product.name}",
                        tint = MaterialTheme.colorScheme.primary // Use primary color for edit
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { onDeleteClick(product) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete ${product.name}",
                        tint = MaterialTheme.colorScheme.error // Use error color for delete
                    )
                }
            }
        }
    }
}

// --- Preview ---
//@Preview(showBackground = true)
//@Composable
//fun PreviewProductAdminCard() {
//    val sampleProduct = Product(
//        id = 12,
//        name = "Organic Green Tea - 250g Pack",
//        description = "High-quality organic green tea leaves.",
//        price = 12.99,
//        imageUrl = "https://via.placeholder.com/150/FF5733/FFFFFF?text=GreenTea", // Placeholder image URL
//        category = "Beverages",
//        ratings = 2.3
//}
//    // Using MaterialTheme for preview to apply typography and colors
//    MaterialTheme { // Use MaterialTheme if you're on M2, Material3Theme if on M3
//        ProductAdminCard(
//            product = sampleProduct,
//            onEditClick = { product -> println("Edit clicked for ${product.name}") },
//            onDeleteClick = { product -> println("Delete clicked for ${product.name}") }
//        )
//    }
//}