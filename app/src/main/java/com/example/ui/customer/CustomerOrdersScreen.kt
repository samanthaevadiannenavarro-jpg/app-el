package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.model.OrderEntity
import com.example.data.repository.ElayaRepository
import com.example.ui.components.BinanDeliveryMap
import com.example.ui.components.ElayaStatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrdersScreen(
    repository: ElayaRepository,
    selectedTrackingOrderId: String? = null,
    onNavigateToMarketplace: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val orders by repository.getCustomerOrders(currentUser?.id ?: "").collectAsStateWithLifecycle(emptyList())
    var viewingOrderForTracking by remember { mutableStateOf<OrderEntity?>(null) }

    // If an order ID was passed from checkout, auto-open tracking
    LaunchedEffect(selectedTrackingOrderId, orders) {
        if (selectedTrackingOrderId != null && viewingOrderForTracking == null) {
            val matched = orders.find { it.id == selectedTrackingOrderId }
            if (matched != null) {
                viewingOrderForTracking = matched
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
    ) {
        // Top Bar
        Surface(
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "My Flower Orders",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ElayaTextPrimary
                    )
                    Text(
                        text = "Fulfillment & Live GPS Tracking in Biñan",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElayaTextSecondary
                    )
                }
            }
        }

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(ElayaPinkLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ReceiptLong,
                            contentDescription = null,
                            tint = ElayaRoseDark,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Text(
                        text = "No Orders Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your placed orders and live delivery tracking will appear here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElayaTextSecondary
                    )
                    Button(
                        onClick = onNavigateToMarketplace,
                        colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start Shopping")
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(orders, key = { it.id }) { order ->
                    CustomerOrderCard(
                        order = order,
                        onTrackClick = { viewingOrderForTracking = order }
                    )
                }
            }
        }
    }

    // Live Tracking Dialog / Modal
    if (viewingOrderForTracking != null) {
        val order = viewingOrderForTracking!!
        ModalBottomSheet(
            onDismissRequest = { viewingOrderForTracking = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tracking Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Order #${order.id}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ElayaTextPrimary
                        )
                        Text(
                            text = "From ${order.shopName} • ${order.orderDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElayaTextSecondary
                        )
                    }
                    ElayaStatusBadge(status = order.orderStatus)
                }

                // Interactive Biñan Google Maps Style Component
                BinanDeliveryMap(
                    originShopName = order.shopName,
                    originBarangay = "San Vicente",
                    destCustomerName = order.customerName,
                    destBarangay = order.barangay,
                    orderStatus = order.orderStatus,
                    riderName = order.riderName,
                    etaMinutes = order.etaMinutes
                )

                // Dispatch & Rider Card
                Surface(
                    color = ElayaPinkLight,
                    shape = RoundedCornerShape(16.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PersonPin, contentDescription = null, tint = ElayaRoseDark)
                            }
                            Column {
                                Text(
                                    text = order.riderName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ElayaTextPrimary
                                )
                                Text(
                                    text = "${order.riderVehicle} • Biñan Express",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElayaTextSecondary
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalIconButton(
                                onClick = { /* Call action */ },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call", tint = StatusSuccess)
                            }
                            FilledTonalIconButton(
                                onClick = { /* Message action */ },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "Message", tint = ElayaRoseDark)
                            }
                        }
                    }
                }

                // Fulfillment Timeline Stages
                Text(
                    text = "Delivery Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val stages = listOf(
                    Pair("Order Placed", "Payment validated & received by florist"),
                    Pair("Preparing", "Florist arranging fresh stems & wrapping"),
                    Pair("Ready for Delivery", "Packaged in water vials & quality inspected"),
                    Pair("Out for Delivery", "Rider Dennis transit on Laguna National Highway"),
                    Pair("Completed", "Delivered safely to recipient")
                )

                val currentStageIdx = when (order.orderStatus.lowercase()) {
                    "pending" -> 0
                    "confirmed" -> 0
                    "preparing" -> 1
                    "ready for delivery" -> 2
                    "out for delivery" -> 3
                    "completed" -> 4
                    else -> 0
                }

                stages.forEachIndexed { idx, (stageTitle, stageDesc) ->
                    val isDone = idx <= currentStageIdx
                    val isCurrent = idx == currentStageIdx

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(
                                        if (isDone) ElayaRoseDark else Color(0xFFE2E8F0),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                            if (idx < stages.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(30.dp)
                                        .background(if (idx < currentStageIdx) ElayaRoseDark else Color(0xFFE2E8F0))
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(bottom = 12.dp)) {
                            Text(
                                text = stageTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCurrent) ElayaRoseDark else ElayaTextPrimary
                            )
                            Text(
                                text = stageDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = ElayaTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun CustomerOrderCard(
    order: OrderEntity,
    onTrackClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_order_card_${order.id}")
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, tint = ElayaPinkPrimary)
                Text(
                    text = order.shopName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = order.productsSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = ElayaTextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = ElayaRoseDark, modifier = Modifier.size(16.dp))
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
                        text = "Total (${order.paymentStatus})",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElayaTextSecondary
                    )
                    Text(
                        text = "₱${"%,.2f".format(order.totalPrice)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElayaRoseDark
                    )
                }

                Button(
                    onClick = onTrackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("track_order_${order.id}")
                ) {
                    Icon(Icons.Default.DirectionsBike, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Live GPS Track", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
