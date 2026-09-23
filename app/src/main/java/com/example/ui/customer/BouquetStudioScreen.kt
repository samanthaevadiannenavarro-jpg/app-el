package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CartItemEntity
import com.example.data.model.ProductEntity
import com.example.data.repository.ElayaRepository
import com.example.ui.components.Interactive3DBouquetCanvas
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun BouquetStudioScreen(
    repository: ElayaRepository,
    onNavigateToCart: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val allProducts by repository.getAllProducts().collectAsStateWithLifecycle(emptyList())

    // Filter available wrappings added by shop owners!
    val availableWrappings = remember(allProducts) {
        val wraps = allProducts.filter { it.productType == "wrapping" && it.status.equals("Available", ignoreCase = true) }
        if (wraps.isEmpty()) {
            listOf(
                ProductEntity(
                    id = "default-w1",
                    shopId = "shop-1",
                    ownerId = "owner-1",
                    productName = "Pastel Pink Wrapper",
                    productType = "wrapping",
                    category = "Wrapping",
                    description = "Blush matte wrapper",
                    imageUrl = "",
                    price = 45.0,
                    stock = 100,
                    wrapColorStyle = "Pastel Pink"
                )
            )
        } else wraps
    }

    // Flower selection state
    val flowerTypes = listOf("Ecuadorian Roses", "Dutch Tulips", "Sunflowers", "Soft Carnations", "Stargazer Lilies")
    var selectedFlowerType by remember { mutableStateOf(flowerTypes[0]) }

    val colorOptions = listOf(
        Pair("Blush Pink", Color(0xFFFF85A1)),
        Pair("Crimson Red", Color(0xFFDC2626)),
        Pair("Cream White", Color(0xFFFFF7ED)),
        Pair("Sunshine Yellow", Color(0xFFFBBF24)),
        Pair("Lavender Purple", Color(0xFFA855F7)),
        Pair("Peach Coral", Color(0xFFFB923C))
    )
    var selectedColor by remember { mutableStateOf(colorOptions[0]) }

    val countOptions = listOf(6, 12, 18, 24, 36)
    var selectedStemCount by remember { mutableIntStateOf(12) }

    var selectedWrapping by remember { mutableStateOf(availableWrappings.first()) }
    // Update wrapping selection if products list loaded
    LaunchedEffect(availableWrappings) {
        if (!availableWrappings.contains(selectedWrapping) && availableWrappings.isNotEmpty()) {
            selectedWrapping = availableWrappings.first()
        }
    }

    val ribbonOptions = listOf(
        Pair("Ruby Red Satin", Color(0xFFB91C1C)),
        Pair("Blush Rose Silk", Color(0xFFFF758C)),
        Pair("Champagne Gold", Color(0xFFD97706)),
        Pair("Emerald Satin", Color(0xFF0F766E)),
        Pair("Pure White Ribbon", Color(0xFFF8FAFC))
    )
    var selectedRibbon by remember { mutableStateOf(ribbonOptions[1]) }

    var cardMessage by remember { mutableStateOf("To the one who makes life beautiful. Love always!") }
    var addedToCartSnackbar by remember { mutableStateOf(false) }

    // Dynamic price calculation
    val baseFlowerUnitPrice = when (selectedFlowerType) {
        "Ecuadorian Roses" -> 50.0
        "Dutch Tulips" -> 75.0
        "Sunflowers" -> 65.0
        "Soft Carnations" -> 45.0
        "Stargazer Lilies" -> 110.0
        else -> 50.0
    }
    val totalPrice = (baseFlowerUnitPrice * selectedStemCount) + selectedWrapping.price + 50.0 // +50 arrangement & ribbon

    val wrappingColor = when {
        selectedWrapping.productName.contains("Kraft", ignoreCase = true) -> Color(0xFFD4A373)
        selectedWrapping.productName.contains("White", ignoreCase = true) -> Color(0xFFF8FAFC)
        selectedWrapping.productName.contains("Cream", ignoreCase = true) -> Color(0xFFFEF3C7)
        selectedWrapping.productName.contains("Floral", ignoreCase = true) -> Color(0xFFE0C3FC)
        selectedWrapping.productName.contains("Transparent", ignoreCase = true) -> Color(0xFFE2E8F0)
        else -> Color(0xFFFFB4C2)
    }

    Scaffold(
        snackbarHost = {
            if (addedToCartSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = {
                            addedToCartSnackbar = false
                            onNavigateToCart()
                        }) {
                            Text("VIEW CART", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = ElayaRoseDark,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Custom 3D Bouquet added to cart!", color = Color.White)
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estimated Total",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElayaTextSecondary
                        )
                        Text(
                            text = "₱${"%,.2f".format(totalPrice)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElayaRoseDark
                        )
                    }

                    Button(
                        onClick = {
                            val user = currentUser ?: return@Button
                            coroutineScope.launch {
                                val cartItem = CartItemEntity(
                                    id = "cart-${UUID.randomUUID().toString().take(8)}",
                                    customerId = user.id,
                                    productId = "custom-3d-bouquet",
                                    shopId = selectedWrapping.shopId.ifBlank { "shop-1" },
                                    shopName = "Custom Studio (${selectedWrapping.productName})",
                                    productName = "Custom 3D: $selectedStemCount $selectedFlowerType",
                                    productType = "bouquet",
                                    price = totalPrice,
                                    quantity = 1,
                                    selectedColor = selectedColor.first,
                                    selectedWrapping = selectedWrapping.productName,
                                    selectedRibbon = selectedRibbon.first,
                                    flowerCount = selectedStemCount,
                                    cardMessage = cardMessage,
                                    isCustom3D = true
                                )
                                repository.addToCart(cartItem)
                                addedToCartSnackbar = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("add_custom_bouquet_to_cart_button")
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "3D Bouquet Studio",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElayaTextPrimary
                    )
                    Text(
                        text = "Customize stems, colors & wraps with live 3D preview",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElayaTextSecondary
                    )
                }

                FilledTonalIconButton(
                    onClick = onNavigateToCart,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = ElayaPinkLight)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "View Cart", tint = ElayaRoseDark)
                }
            }

            // Interactive 3D Canvas
            Interactive3DBouquetCanvas(
                flowerType = selectedFlowerType,
                flowerColorName = selectedColor.first,
                flowerColor = selectedColor.second,
                stemCount = selectedStemCount,
                wrappingName = selectedWrapping.productName,
                wrappingColor = wrappingColor,
                ribbonName = selectedRibbon.first,
                ribbonColor = selectedRibbon.second
            )

            // Step 1: Flower Variety
            SectionTitle("1. Choose Flower Variety")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                flowerTypes.forEach { type ->
                    FilterChip(
                        selected = selectedFlowerType == type,
                        onClick = { selectedFlowerType = type },
                        label = { Text(type) },
                        leadingIcon = {
                            Icon(Icons.Default.LocalFlorist, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElayaPinkPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Step 2: Flower Color
            SectionTitle("2. Select Flower Bloom Color")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                colorOptions.forEach { (name, color) ->
                    val isSelected = selectedColor.first == name
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedColor = Pair(name, color) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) ElayaRoseDark else Color.LightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (color == Color(0xFFFFF7ED)) Color.Black else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ElayaRoseDark else ElayaTextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Step 3: Stem Quantity
            SectionTitle("3. Stem Count & Size")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                countOptions.forEach { count ->
                    val isSelected = selectedStemCount == count
                    OutlinedButton(
                        onClick = { selectedStemCount = count },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) ElayaPinkLight else Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = if (isSelected) ElayaGradients.floralPrimary else androidx.compose.ui.graphics.SolidColor(Color.LightGray)
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ElayaRoseDark else ElayaTextPrimary
                            )
                            Text(
                                text = "stems",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElayaTextSecondary
                            )
                        }
                    }
                }
            }

            // Step 4: Wrapping Style (Provided by Biñan Flower Shop Owners!)
            SectionTitle("4. Wrapping Paper (From Florist Partners)")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availableWrappings.forEach { wrap ->
                    val isSelected = selectedWrapping.id == wrap.id
                    Card(
                        onClick = { selectedWrapping = wrap },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ElayaPinkLight else Color.White
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = if (isSelected) ElayaGradients.floralPrimary else androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))
                        ),
                        modifier = Modifier.fillMaxWidth()
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
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedWrapping = wrap },
                                    colors = RadioButtonDefaults.colors(selectedColor = ElayaRoseDark)
                                )
                                Column {
                                    Text(
                                        text = wrap.productName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = wrap.wrapColorStyle.ifBlank { wrap.description },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ElayaTextSecondary
                                    )
                                }
                            }
                            Text(
                                text = "+₱${wrap.price.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElayaRoseDark
                            )
                        }
                    }
                }
            }

            // Step 5: Ribbon
            SectionTitle("5. Luxury Satin Ribbon")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ribbonOptions.forEach { (name, color) ->
                    val isSelected = selectedRibbon.first == name
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRibbon = Pair(name, color) },
                        label = { Text(name) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(color, CircleShape)
                            )
                        }
                    )
                }
            }

            // Step 6: Dedication Card
            SectionTitle("6. Free Dedication Message Card")
            OutlinedTextField(
                value = cardMessage,
                onValueChange = { cardMessage = it },
                label = { Text("Personal greeting / note for recipient") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = ElayaTextPrimary,
        modifier = Modifier.padding(top = 6.dp)
    )
}
