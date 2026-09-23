package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.OrderEntity
import com.example.data.repository.ElayaRepository
import com.example.ui.components.ElayaStatusBadge
import com.example.ui.theme.*

@Composable
fun AdminOrdersScreen(
    repository: ElayaRepository
) {
    val allOrders by repository.getAllOrders().collectAsStateWithLifecycle(emptyList())
    var statusFilter by remember { mutableStateOf("all") }

    val filtered = remember(allOrders, statusFilter) {
        if (statusFilter == "all") allOrders
        else allOrders.filter { it.orderStatus.equals(statusFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
    ) {
        Surface(
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "System-Wide Order Dispatch",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AdminIndigoDark
                )
                Text(
                    text = "Real-time dispatch across all 24 Biñan barangays (${allOrders.size} total orders)",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElayaTextSecondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("all", "Pending", "Preparing", "Ready for Delivery", "Out for Delivery", "Completed").forEach { st ->
                        FilterChip(
                            selected = statusFilter == st,
                            onClick = { statusFilter = st },
                            label = { Text(if (st == "all") "All Orders" else st) }
                        )
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered, key = { it.id }) { order ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_order_${order.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Order #${order.id}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            ElayaStatusBadge(status = order.orderStatus)
                        }

                        Text(
                            text = "Shop: ${order.shopName}  ➜  Customer: ${order.customerName}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ElayaTextPrimary
                        )

                        Text(
                            text = "Items: ${order.productsSummary}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElayaTextSecondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = AdminIndigoPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = order.deliveryAddress,
                                style = MaterialTheme.typography.bodySmall,
                                color = ElayaTextSecondary
                            )
                        }

                        Divider(color = ElayaBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rider: ${order.riderName} (ETA ~${order.etaMinutes}m)",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElayaTextSecondary
                            )
                            Text(
                                text = "₱${"%,.2f".format(order.totalPrice)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = AdminIndigoDark
                            )
                        }
                    }
                }
            }
        }
    }
}
