package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.CartItemEntity
import com.example.data.model.FlowerShopEntity
import com.example.data.model.ProductEntity
import com.example.data.repository.ElayaRepository
import com.example.ui.components.ElayaStatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowerMarketplaceScreen(
    repository: ElayaRepository,
    onNavigateToStudio: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val allProducts by repository.getAllProducts().collectAsStateWithLifecycle(emptyList())
    val allShops by repository.getAllShops().collectAsStateWithLifecycle(emptyList())
    val favoriteIds by repository.getFavoriteIds(currentUser?.id ?: "").collectAsStateWithLifecycle(emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("all") } // "all", "flower", "bouquet", "wrapping"
    var selectedShopFilter by remember { mutableStateOf<String?>(null) } // null = all shops

    var selectedProductForDetail by remember { mutableStateOf<ProductEntity?>(null) }

    // Filter products
    val filteredProducts = remember(allProducts, searchQuery, selectedTypeFilter, selectedShopFilter) {
        allProducts.filter { product ->
            val matchesSearch = searchQuery.isBlank() ||
                    product.productName.contains(searchQuery, ignoreCase = true) ||
                    product.category.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedTypeFilter == "all" || product.productType == selectedTypeFilter
            val matchesShop = selectedShopFilter == null || product.shopId == selectedShopFilter
            matchesSearch && matchesType && matchesShop
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
    ) {
        // Top Bar & Search
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Elaya Marketplace",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElayaRoseDark
                    )
                    Text(
                        text = "Handcrafted fresh flowers in Biñan, Laguna",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElayaTextSecondary
                    )
                }

                FilledTonalIconButton(
                    onClick = onNavigateToCart,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = ElayaPinkLight),
                    modifier = Modifier.testTag("marketplace_cart_button")
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = ElayaRoseDark)
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Ecuadorian roses, sunflowers, bouquets...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElayaTextMuted) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("marketplace_search_input")
            )

            // Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf(
                    Pair("all", "All Products"),
                    Pair("flower", "Fresh Flowers"),
                    Pair("bouquet", "Ready Bouquets"),
                    Pair("wrapping", "Artisan Wrappings")
                )
                categories.forEach { (typeKey, label) ->
                    FilterChip(
                        selected = selectedTypeFilter == typeKey,
                        onClick = { selectedTypeFilter = typeKey },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElayaPinkPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Filter by Biñan Florist Partner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shops:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElayaTextSecondary
                )
                FilterChip(
                    selected = selectedShopFilter == null,
                    onClick = { selectedShopFilter = null },
                    label = { Text("All Florists") }
                )
                allShops.forEach { shop ->
                    FilterChip(
                        selected = selectedShopFilter == shop.id,
                        onClick = { selectedShopFilter = if (selectedShopFilter == shop.id) null else shop.id },
                        label = { Text(shop.shopName) }
                    )
                }
            }
        }

        // 3D Studio Banner Callout
        Card(
            onClick = onNavigateToStudio,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ElayaPinkLight),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("open_3d_studio_banner")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(ElayaGradients.floralPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ViewInAr, contentDescription = null, tint = Color.White)
                    }
                    Column {
                        Text(
                            text = "Design a Custom 3D Bouquet",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElayaRoseDark
                        )
                        Text(
                            text = "Pick flowers, wraps & see real-time 3D simulation",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElayaTextPrimary
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ElayaRoseDark)
            }
        }

        // Product Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredProducts, key = { it.id }) { product ->
                val shop = allShops.find { it.id == product.shopId }
                val isFav = favoriteIds.contains(product.id)

                MarketplaceProductCard(
                    product = product,
                    shopName = shop?.shopName ?: "Biñan Florist",
                    isFavorite = isFav,
                    onToggleFavorite = {
                        currentUser?.id?.let { uid ->
                            coroutineScope.launch { repository.toggleFavorite(uid, product.id) }
                        }
                    },
                    onClick = { selectedProductForDetail = product }
                )
            }
        }
    }

    // Product Details Bottom Sheet Modal
    if (selectedProductForDetail != null) {
        val product = selectedProductForDetail!!
        val shop = allShops.find { it.id == product.shopId }
        var quantity by remember { mutableIntStateOf(1) }
        val colorsList = remember(product.availableColors) {
            product.availableColors.split(",").map { it.trim() }.filter { it.isNotBlank() }
        }
        var chosenColor by remember { mutableStateOf(colorsList.firstOrNull() ?: "Standard") }
        var showAddedNotice by remember { mutableStateOf(false) }

        ModalBottomSheet(
            onDismissRequest = { selectedProductForDetail = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Image & Details
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ElayaPinkLight)
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    ElayaStatusBadge(
                        status = product.status,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.productName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ElayaTextPrimary
                        )
                        Text(
                            text = "Sold by ${shop?.shopName ?: "Partner Florist"} • ${shop?.location ?: "Biñan, Laguna"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElayaTextSecondary
                        )
                    }
                    Text(
                        text = "₱${"%,.2f".format(product.price)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElayaRoseDark
                    )
                }

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElayaTextSecondary
                )

                if (colorsList.isNotEmpty() && product.productType == "flower") {
                    Text(
                        text = "Available Bloom Colors:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colorsList.forEach { col ->
                            FilterChip(
                                selected = chosenColor == col,
                                onClick = { chosenColor = col },
                                label = { Text(col) }
                            )
                        }
                    }
                }

                // Quantity Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Quantity", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        FilledTonalIconButton(
                            onClick = { if (quantity < product.stock) quantity++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }

                // Action Buttons
                Button(
                    onClick = {
                        val user = currentUser ?: return@Button
                        coroutineScope.launch {
                            repository.addToCart(
                                CartItemEntity(
                                    id = "cart-${UUID.randomUUID().toString().take(8)}",
                                    customerId = user.id,
                                    productId = product.id,
                                    shopId = product.shopId,
                                    shopName = shop?.shopName ?: "Biñan Florist",
                                    productName = product.productName,
                                    productType = product.productType,
                                    price = product.price,
                                    quantity = quantity,
                                    selectedColor = chosenColor,
                                    imageUrl = product.imageUrl
                                )
                            )
                            showAddedNotice = true
                            selectedProductForDetail = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("add_product_to_cart_button")
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add to Cart • ₱${"%,.2f".format(product.price * quantity)}", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MarketplaceProductCard(
    product: ProductEntity,
    shopName: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}")
    ) {
        Column {
            // Image with Favorite Heart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(ElayaPinkLight)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) ElayaRoseCrimson else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(topStart = 8.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = shopName,
                    style = MaterialTheme.typography.bodySmall,
                    color = ElayaTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₱${"%,.0f".format(product.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElayaRoseDark
                    )
                    Text(
                        text = "${product.stock} in stock",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (product.stock > 10) StatusSuccess else StatusWarning
                    )
                }
            }
        }
    }
}
