package com.example.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.ProductEntity
import com.example.data.repository.ElayaRepository
import com.example.ui.components.ElayaStatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerProductsScreen(
    repository: ElayaRepository,
    onOpenAddFlower: () -> Unit,
    onOpenAddBouquet: () -> Unit,
    onOpenAddWrapping: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val currentShop by repository.currentShop.collectAsStateWithLifecycle()
    val ownerId = currentUser?.id ?: ""

    val ownerProducts by repository.getProductsByOwner(ownerId).collectAsStateWithLifecycle(emptyList())

    var selectedFilter by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    val filtered = remember(ownerProducts, selectedFilter, searchQuery) {
        ownerProducts.filter { prod ->
            val matchType = selectedFilter == "all" || prod.productType == selectedFilter
            val matchSearch = searchQuery.isBlank() || prod.productName.contains(searchQuery, ignoreCase = true)
            matchType && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
    ) {
        // Top Action Bar
        Surface(
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Products & Inventory",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OwnerEmeraldDark
                        )
                        Text(
                            text = "${currentShop?.shopName ?: "My Flower Shop"} • ${ownerProducts.size} items",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElayaTextSecondary
                        )
                    }
                }

                // Add Buttons Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenAddFlower,
                        colors = ButtonDefaults.buttonColors(containerColor = OwnerEmeraldDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("owner_add_flower_btn")
                    ) {
                        Icon(Icons.Default.LocalFlorist, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add Flower")
                    }

                    Button(
                        onClick = onOpenAddBouquet,
                        colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("owner_add_bouquet_btn")
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add Bouquet")
                    }

                    OutlinedButton(
                        onClick = onOpenAddWrapping,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("owner_add_wrapping_btn")
                    ) {
                        Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add Wrapping")
                    }
                }

                // Search & Filter
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter my products...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Pair("all", "All (${ownerProducts.size})"),
                        Pair("flower", "Flowers (${ownerProducts.count { it.productType == "flower" }})"),
                        Pair("bouquet", "Bouquets (${ownerProducts.count { it.productType == "bouquet" }})"),
                        Pair("wrapping", "Wrappings (${ownerProducts.count { it.productType == "wrapping" }})")
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { selectedFilter = key },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }

        // Product List
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Outlined.Inventory2, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Text("No products found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Use the buttons above to add flowers, bouquets, or custom wraps to your shop.", color = ElayaTextSecondary)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.id }) { product ->
                    OwnerProductCard(
                        product = product,
                        onEdit = { editingProduct = product },
                        onDelete = { productToDelete = product }
                    )
                }
            }
        }
    }

    // Edit Product Dialog
    if (editingProduct != null) {
        EditProductDialog(
            product = editingProduct!!,
            onDismiss = { editingProduct = null },
            onSave = { updated ->
                coroutineScope.launch {
                    repository.updateProduct(updated)
                    editingProduct = null
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (productToDelete != null) {
        val prod = productToDelete!!
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product") },
            text = { Text("Are you sure you want to remove '${prod.productName}' from your flower shop catalog? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            repository.deleteOwnerProduct(prod.id, ownerId)
                            productToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun OwnerProductCard(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("owner_product_${product.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElayaTextPrimary
                    )
                }

                Surface(
                    color = OwnerEmeraldLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = product.productType.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = OwnerEmeraldDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₱${"%,.2f".format(product.price)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = OwnerEmeraldDark
                    )
                    Text(
                        text = "Stock: ${product.stock}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (product.stock > 5) StatusSuccess else StatusWarning,
                        fontWeight = FontWeight.SemiBold
                    )
                    ElayaStatusBadge(status = product.status)
                }
            }

            // Action Icons
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = OwnerEmeraldDark)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFDC2626))
                }
            }
        }
    }
}
