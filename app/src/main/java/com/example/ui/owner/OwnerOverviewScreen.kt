package com.example.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.repository.ElayaRepository
import com.example.ui.components.ElayaStatCard
import com.example.ui.theme.*

@Composable
fun OwnerOverviewScreen(
    repository: ElayaRepository,
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onOpenAddFlower: () -> Unit,
    onOpenAddBouquet: () -> Unit,
    onOpenAddWrapping: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val currentShop by repository.currentShop.collectAsStateWithLifecycle()
    val ownerId = currentUser?.id ?: ""
    val shopId = currentShop?.id ?: "shop-1"

    val products by repository.getProductsByOwner(ownerId).collectAsStateWithLifecycle(emptyList())
    val orders by repository.getShopOrders(shopId).collectAsStateWithLifecycle(emptyList())

    val totalSales = orders.filter { it.orderStatus == "Completed" || it.paymentStatus.contains("Paid") }.sumOf { it.totalPrice }
    val activeOrdersCount = orders.count { it.orderStatus != "Completed" && it.orderStatus != "Cancelled" }
    val lowStockCount = products.count { it.stock <= 5 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = OwnerEmeraldDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "FLOWER SHOP OWNER DASHBOARD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = currentShop?.shopName ?: "Bella Flora Biñan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "Location: ${currentShop?.location ?: "Biñan, Laguna"} • Rating 4.9 ★",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElayaStatCard(
                title = "Total Revenue",
                value = "₱${"%,.0f".format(totalSales)}",
                icon = Icons.Default.Payments,
                iconColor = OwnerEmeraldDark,
                subtitle = "From ${orders.size} customer orders",
                modifier = Modifier.weight(1f)
            )

            ElayaStatCard(
                title = "Active Orders",
                value = "$activeOrdersCount",
                icon = Icons.Default.LocalShipping,
                iconColor = ElayaRoseDark,
                subtitle = "Fulfillment in progress",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElayaStatCard(
                title = "Catalog Items",
                value = "${products.size}",
                icon = Icons.Default.LocalFlorist,
                iconColor = OwnerEmeraldPrimary,
                subtitle = "Flowers & Bouquets",
                modifier = Modifier.weight(1f)
            )

            ElayaStatCard(
                title = "Low Stock Alert",
                value = "$lowStockCount",
                icon = Icons.Default.WarningAmber,
                iconColor = if (lowStockCount > 0) StatusWarning else StatusSuccess,
                subtitle = "Needs replenishment",
                modifier = Modifier.weight(1f)
            )
        }

        // Quick Actions Banner
        Text(
            text = "Product Catalog Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ElayaTextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onOpenAddFlower,
                colors = ButtonDefaults.buttonColors(containerColor = OwnerEmeraldDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).testTag("quick_add_flower_btn")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocalFlorist, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("Add Flower", style = MaterialTheme.typography.labelSmall)
                }
            }

            Button(
                onClick = onOpenAddBouquet,
                colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).testTag("quick_add_bouquet_btn")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("Add Bouquet", style = MaterialTheme.typography.labelSmall)
                }
            }

            Button(
                onClick = onOpenAddWrapping,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).testTag("quick_add_wrapping_btn")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("Add Wrap", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Biñan Local Coverage Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.LocationCity, contentDescription = null, tint = OwnerEmeraldPrimary)
                    Text(
                        text = "Biñan City Delivery Coverage",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Your flower shop is connected to all 24 Biñan barangays including San Vicente, Zapote, Canlalay, Casile, San Francisco (Southwoods), and Santo Tomas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElayaTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
