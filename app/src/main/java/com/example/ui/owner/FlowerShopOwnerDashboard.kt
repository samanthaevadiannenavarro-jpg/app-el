package com.example.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ProductEntity
import com.example.data.model.UserRole
import com.example.data.repository.ElayaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun FlowerShopOwnerDashboard(
    repository: ElayaRepository,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val currentShop by repository.currentShop.collectAsStateWithLifecycle()
    val ownerId = currentUser?.id ?: ""
    val shopId = currentShop?.id ?: "shop-1"

    var selectedTab by remember { mutableIntStateOf(0) } // 0=Overview, 1=Products, 2=Orders, 3=Profile

    var showAddFlowerDialog by remember { mutableStateOf(false) }
    var showAddBouquetDialog by remember { mutableStateOf(false) }
    var showAddWrappingDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Overview"
                        )
                    },
                    label = { Text("Overview") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OwnerEmeraldDark,
                        selectedTextColor = OwnerEmeraldDark,
                        indicatorColor = OwnerEmeraldLight
                    ),
                    modifier = Modifier.testTag("nav_owner_overview")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Default.Inventory else Icons.Outlined.Inventory,
                            contentDescription = "Products"
                        )
                    },
                    label = { Text("Products") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OwnerEmeraldDark,
                        selectedTextColor = OwnerEmeraldDark,
                        indicatorColor = OwnerEmeraldLight
                    ),
                    modifier = Modifier.testTag("nav_owner_products")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Default.ReceiptLong else Icons.Outlined.ReceiptLong,
                            contentDescription = "Orders"
                        )
                    },
                    label = { Text("Orders") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OwnerEmeraldDark,
                        selectedTextColor = OwnerEmeraldDark,
                        indicatorColor = OwnerEmeraldLight
                    ),
                    modifier = Modifier.testTag("nav_owner_orders")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 3) Icons.Default.Store else Icons.Outlined.Store,
                            contentDescription = "My Shop"
                        )
                    },
                    label = { Text("Shop Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OwnerEmeraldDark,
                        selectedTextColor = OwnerEmeraldDark,
                        indicatorColor = OwnerEmeraldLight
                    ),
                    modifier = Modifier.testTag("nav_owner_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> OwnerOverviewScreen(
                    repository = repository,
                    onNavigateToProducts = { selectedTab = 1 },
                    onNavigateToOrders = { selectedTab = 2 },
                    onOpenAddFlower = { showAddFlowerDialog = true },
                    onOpenAddBouquet = { showAddBouquetDialog = true },
                    onOpenAddWrapping = { showAddWrappingDialog = true }
                )
                1 -> OwnerProductsScreen(
                    repository = repository,
                    onOpenAddFlower = { showAddFlowerDialog = true },
                    onOpenAddBouquet = { showAddBouquetDialog = true },
                    onOpenAddWrapping = { showAddWrappingDialog = true }
                )
                2 -> OwnerOrdersScreen(
                    repository = repository
                )
                3 -> OwnerShopProfileScreen(
                    repository = repository,
                    onLogout = onLogout,
                    onSwitchRole = onSwitchRole
                )
            }
        }

        // Add Flower Dialog
        if (showAddFlowerDialog) {
            AddFlowerDialog(
                onDismiss = { showAddFlowerDialog = false },
                onAddFlower = { name, category, desc, image, colors, price, stock, availability ->
                    coroutineScope.launch {
                        val product = ProductEntity(
                            id = "flower-${UUID.randomUUID().toString().take(8)}",
                            shopId = shopId,
                            ownerId = ownerId,
                            productName = name,
                            productType = "flower",
                            category = category,
                            description = desc,
                            imageUrl = image,
                            availableColors = colors,
                            price = price,
                            stock = stock,
                            status = availability
                        )
                        repository.addFlowerProduct(product)
                        showAddFlowerDialog = false
                    }
                }
            )
        }

        // Add Bouquet Dialog
        if (showAddBouquetDialog) {
            AddBouquetDialog(
                onDismiss = { showAddBouquetDialog = false },
                onAddBouquet = { name, image, desc, flowersInc, count, wrap, ribbon, price, stock, availability ->
                    coroutineScope.launch {
                        val product = ProductEntity(
                            id = "bouquet-${UUID.randomUUID().toString().take(8)}",
                            shopId = shopId,
                            ownerId = ownerId,
                            productName = name,
                            productType = "bouquet",
                            category = "Bouquets",
                            description = desc,
                            imageUrl = image,
                            flowersIncluded = flowersInc,
                            numberOfFlowers = count,
                            wrapping = wrap,
                            ribbon = ribbon,
                            price = price,
                            stock = stock,
                            status = availability
                        )
                        repository.addBouquetProduct(product)
                        showAddBouquetDialog = false
                    }
                }
            )
        }

        // Add Wrapping Dialog
        if (showAddWrappingDialog) {
            AddWrappingDialog(
                onDismiss = { showAddWrappingDialog = false },
                onAddWrapping = { name, image, colorStyle, price, stock, availability ->
                    coroutineScope.launch {
                        val product = ProductEntity(
                            id = "wrap-${UUID.randomUUID().toString().take(8)}",
                            shopId = shopId,
                            ownerId = ownerId,
                            productName = name,
                            productType = "wrapping",
                            category = "Wrapping",
                            description = "Artisan bouquet wrap ($colorStyle)",
                            imageUrl = image,
                            wrapColorStyle = colorStyle,
                            price = price,
                            stock = stock,
                            status = availability
                        )
                        repository.addWrappingProduct(product)
                        showAddWrappingDialog = false
                    }
                }
            )
        }
    }
}
