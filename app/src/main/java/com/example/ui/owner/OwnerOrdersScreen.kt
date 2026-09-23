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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.OrderEntity
import com.example.data.repository.ElayaRepository
import com.example.ui.components.ElayaStatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerOrdersScreen(
    repository: ElayaRepository
) {
    val coroutineScope = rememberCoroutineScope()
    val currentShop by repository.currentShop.collectAsStateWithLifecycle()
    val shopId = currentShop?.id ?: "shop-1"

    val shopOrders by repository.getShopOrders(shopId).collectAsStateWithLifecycle(emptyList())

    var selectedFilter by remember { mutableStateOf("all") }
    var managingOrder by remember { mutableStateOf<OrderEntity?>(null) }

    val filteredOrders = remember(shopOrders, selectedFilter) {
        if (selectedFilter == "all") shopOrders
        else shopOrders.filter { it.orderStatus.equals(selectedFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
    ) {
        // Header
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
                    text = "Customer Orders & Dispatch",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OwnerEmeraldDark
                )
                Text(
                    text = "Manage fulfillment, floral preparation, and rider dispatch in Biñan",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElayaTextSecondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("all", "Pending", "Preparing", "Ready for Delivery", "Out for Delivery", "Completed").forEach { status ->
                        FilterChip(
                            selected = selectedFilter == status,
                            onClick = { selectedFilter = status },
                            label = { Text(if (status == "all") "All (${shopOrders.size})" else status) }
                        )
                    }
                }
            }
        }

        if (filteredOrders.isEmpty()) {
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
                    Icon(Icons.Outlined.Assignment, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Text("No orders in this status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OwnerOrderCard(
                        order = order,
                        onManage = { managingOrder = order }
                    )
                }
            }
        }
    }

    // Manage Fulfillment Sheet
    if (managingOrder != null) {
        val order = managingOrder!!
        var currentStatus by remember { mutableStateOf(order.orderStatus) }
        var riderName by remember { mutableStateOf(order.riderName) }
        var riderPhone by remember { mutableStateOf(order.riderPhone) }
        var etaMinutesText by remember { mutableStateOf(order.etaMinutes.toString()) }

        ModalBottomSheet(
            onDismissRequest = { managingOrder = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Update Fulfillment • #${order.id}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OwnerEmeraldDark
                )

                Text(
                    text = "Customer: ${order.customerName} (${order.customerPhone})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Delivery Location: ${order.deliveryAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElayaTextSecondary
                )

                Divider(color = ElayaBorder)

                Text(
                    text = "Fulfillment Status:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                val statuses = listOf("Pending", "Confirmed", "Preparing", "Ready for Delivery", "Out for Delivery", "Completed")
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    statuses.forEach { st ->
                        Surface(
                            onClick = { currentStatus = st },
                            color = if (currentStatus == st) OwnerEmeraldLight else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = st,
                                    fontWeight = if (currentStatus == st) FontWeight.Bold else FontWeight.Normal,
                                    color = if (currentStatus == st) OwnerEmeraldDark else ElayaTextPrimary
                                )
                                RadioButton(
                                    selected = currentStatus == st,
                                    onClick = { currentStatus = st },
                                    colors = RadioButtonDefaults.colors(selectedColor = OwnerEmeraldDark)
                                )
                            }
                        }
                    }
                }

                // Dispatch Rider Assignment (if preparing or out for delivery)
                Text(
                    text = "Assign Delivery Rider / Courier:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = riderName,
                        onValueChange = { riderName = it },
                        label = { Text("Rider Name") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = etaMinutesText,
                        onValueChange = { etaMinutesText = it },
                        label = { Text("ETA (Mins)") },
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Button(
                    onClick = {
                        val eta = etaMinutesText.toIntOrNull() ?: order.etaMinutes
                        coroutineScope.launch {
                            repository.updateOrderStatus(order.id, currentStatus)
                            val updatedOrder = order.copy(
                                orderStatus = currentStatus,
                                riderName = riderName,
                                etaMinutes = eta
                            )
                            repository.updateOrder(updatedOrder)
                            managingOrder = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OwnerEmeraldDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_fulfillment_update")
                ) {
                    Text("Save & Update Status", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun OwnerOrderCard(
    order: OrderEntity,
    onManage: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("owner_order_${order.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElayaTextPrimary
                    )
                    Text(
                        text = order.orderDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = ElayaTextMuted
                    )
                }
                ElayaStatusBadge(status = order.orderStatus)
            }

            Divider(color = ElayaBorder)

            Text(
                text = "Customer: ${order.customerName} (${order.customerPhone})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = ElayaTextPrimary
            )

            Text(
                text = "Items: ${order.productsSummary}",
                style = MaterialTheme.typography.bodyMedium,
                color = ElayaTextSecondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = OwnerEmeraldPrimary, modifier = Modifier.size(16.dp))
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
                Column {
                    Text(
                        text = order.paymentStatus,
                        style = MaterialTheme.typography.labelSmall,
                        color = ElayaTextSecondary
                    )
                    Text(
                        text = "₱${"%,.2f".format(order.totalPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = OwnerEmeraldDark
                    )
                }

                Button(
                    onClick = onManage,
                    colors = ButtonDefaults.buttonColors(containerColor = OwnerEmeraldDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("manage_order_${order.id}")
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Update Status")
                }
            }
        }
    }
}
