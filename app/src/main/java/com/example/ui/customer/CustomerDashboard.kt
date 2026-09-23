package com.example.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.data.repository.ElayaRepository
import com.example.ui.theme.*

@Composable
fun CustomerDashboard(
    repository: ElayaRepository,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit
) {
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val cartItems by repository.getCart(currentUser?.id ?: "").collectAsStateWithLifecycle(emptyList())
    val customerOrders by repository.getCustomerOrders(currentUser?.id ?: "").collectAsStateWithLifecycle(emptyList())

    var selectedTab by remember { mutableIntStateOf(0) } // 0=Marketplace, 1=3D Studio, 2=Cart, 3=Orders, 4=Profile
    var trackingOrderId by remember { mutableStateOf<String?>(null) }

    val hasActiveOrder = customerOrders.any { it.orderStatus == "Out for Delivery" || it.orderStatus == "Preparing" }

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
                            imageVector = if (selectedTab == 0) Icons.Default.Storefront else Icons.Outlined.Storefront,
                            contentDescription = "Marketplace"
                        )
                    },
                    label = { Text("Shop") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElayaRoseDark,
                        selectedTextColor = ElayaRoseDark,
                        indicatorColor = ElayaPinkLight
                    ),
                    modifier = Modifier.testTag("nav_customer_shop")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Default.ViewInAr else Icons.Outlined.ViewInAr,
                            contentDescription = "3D Studio"
                        )
                    },
                    label = { Text("3D Studio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElayaRoseDark,
                        selectedTextColor = ElayaRoseDark,
                        indicatorColor = ElayaPinkLight
                    ),
                    modifier = Modifier.testTag("nav_customer_studio")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartItems.isNotEmpty()) {
                                    Badge(containerColor = ElayaRoseDark) {
                                        Text("${cartItems.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Default.ShoppingCart else Icons.Outlined.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElayaRoseDark,
                        selectedTextColor = ElayaRoseDark,
                        indicatorColor = ElayaPinkLight
                    ),
                    modifier = Modifier.testTag("nav_customer_cart")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (hasActiveOrder) {
                                    Badge(containerColor = StatusSuccess) {
                                        Text("LIVE")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == 3) Icons.Default.LocalShipping else Icons.Outlined.LocalShipping,
                                contentDescription = "Orders"
                            )
                        }
                    },
                    label = { Text("Orders") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElayaRoseDark,
                        selectedTextColor = ElayaRoseDark,
                        indicatorColor = ElayaPinkLight
                    ),
                    modifier = Modifier.testTag("nav_customer_orders")
                )

                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 4) Icons.Default.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElayaRoseDark,
                        selectedTextColor = ElayaRoseDark,
                        indicatorColor = ElayaPinkLight
                    ),
                    modifier = Modifier.testTag("nav_customer_profile")
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
                0 -> FlowerMarketplaceScreen(
                    repository = repository,
                    onNavigateToStudio = { selectedTab = 1 },
                    onNavigateToCart = { selectedTab = 2 }
                )
                1 -> BouquetStudioScreen(
                    repository = repository,
                    onNavigateToCart = { selectedTab = 2 }
                )
                2 -> CartScreen(
                    repository = repository,
                    onNavigateToMarketplace = { selectedTab = 0 },
                    onOrderPlaced = { orderId ->
                        trackingOrderId = orderId
                        selectedTab = 3 // jump to Orders & live tracking!
                    }
                )
                3 -> CustomerOrdersScreen(
                    repository = repository,
                    selectedTrackingOrderId = trackingOrderId,
                    onNavigateToMarketplace = { selectedTab = 0 }
                )
                4 -> CustomerProfileScreen(
                    repository = repository,
                    onLogout = onLogout,
                    onSwitchRole = onSwitchRole
                )
            }
        }
    }
}
