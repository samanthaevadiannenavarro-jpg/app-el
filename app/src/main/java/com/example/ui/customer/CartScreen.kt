package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BinanLocations
import com.example.data.model.CartItemEntity
import com.example.data.model.OrderEntity
import com.example.data.repository.ElayaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    repository: ElayaRepository,
    onNavigateToMarketplace: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val cartItems by repository.getCart(currentUser?.id ?: "").collectAsStateWithLifecycle(emptyList())

    var selectedBarangay by remember { mutableStateOf(currentUser?.defaultBarangay ?: "San Vicente") }
    var streetAddress by remember { mutableStateOf(currentUser?.defaultAddress ?: "San Vicente, Biñan, Laguna") }
    var recipientPhone by remember { mutableStateOf(currentUser?.phone ?: "0917-234-5678") }
    var selectedPaymentMethod by remember { mutableStateOf("GCash") }
    var voucherCode by remember { mutableStateOf("") }
    var discountApplied by remember { mutableDoubleStateOf(0.0) }
    var voucherMessage by remember { mutableStateOf<String?>(null) }
    var showCheckoutSheet by remember { mutableStateOf(false) }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val barangayInfo = remember(selectedBarangay) { BinanLocations.findBarangay(selectedBarangay) }
    val deliveryFee = barangayInfo.deliveryFee
    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val finalTotal = (subtotal + deliveryFee - discountApplied).coerceAtLeast(0.0)

    Scaffold(
        bottomBar = {
            if (cartItems.isNotEmpty()) {
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
                                text = "Total (${cartItems.size} items)",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElayaTextSecondary
                            )
                            Text(
                                text = "₱${"%,.2f".format(finalTotal)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = ElayaRoseDark
                            )
                        }

                        Button(
                            onClick = { showCheckoutSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("proceed_checkout_button")
                        ) {
                            Text("Proceed to Checkout", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(ElayaPinkLight, RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            tint = ElayaRoseDark,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Text(
                        text = "Your Cart is Empty",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ElayaTextPrimary
                    )
                    Text(
                        text = "Explore fresh flowers or customize a 3D bouquet in our studio!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElayaTextSecondary
                    )
                    Button(
                        onClick = onNavigateToMarketplace,
                        colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("empty_cart_browse_button")
                    ) {
                        Text("Browse Marketplace")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(ElayaBackground),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                item {
                    Text(
                        text = "Shopping Cart",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElayaTextPrimary
                    )
                }

                // Delivery Destination Banner
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = ElayaRoseDark)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Delivering to Biñan, Laguna",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElayaTextSecondary
                                )
                                Text(
                                    text = "$selectedBarangay (${barangayInfo.landmark})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ElayaTextPrimary
                                )
                            }
                            Text(
                                text = "₱${deliveryFee.toInt()} fee",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = ElayaRoseDark
                            )
                        }
                    }
                }

                // Items list
                items(cartItems, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = {
                            coroutineScope.launch {
                                repository.addToCart(item.copy(quantity = item.quantity + 1))
                            }
                        },
                        onDecrease = {
                            coroutineScope.launch {
                                if (item.quantity > 1) {
                                    repository.addToCart(item.copy(quantity = item.quantity - 1))
                                } else {
                                    repository.removeFromCart(item.id)
                                }
                            }
                        },
                        onRemove = {
                            coroutineScope.launch { repository.removeFromCart(item.id) }
                        }
                    )
                }

                // Voucher Promo Code Input
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = voucherCode,
                                    onValueChange = { voucherCode = it },
                                    placeholder = { Text("Promo code (e.g. ELAYABINAN)") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        if (voucherCode.trim().equals("ELAYABINAN", ignoreCase = true)) {
                                            discountApplied = 100.0
                                            voucherMessage = "₱100 Laguna launch discount applied!"
                                        } else {
                                            voucherMessage = "Invalid code. Try ELAYABINAN"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElayaPinkPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Apply")
                                }
                            }
                            if (voucherMessage != null) {
                                Text(
                                    text = voucherMessage!!,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (discountApplied > 0) StatusSuccess else StatusWarning
                                )
                            }
                        }
                    }
                }

                // Order Price Breakdown
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Order Summary", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Divider(color = ElayaBorder)
                            SummaryRow("Merchandise Subtotal", "₱${"%,.2f".format(subtotal)}")
                            SummaryRow("Biñan Courier Delivery Fee", "₱${"%,.2f".format(deliveryFee)}")
                            if (discountApplied > 0) {
                                SummaryRow("Voucher Discount", "-₱${"%,.2f".format(discountApplied)}", color = StatusSuccess)
                            }
                            Divider(color = ElayaBorder)
                            SummaryRow(
                                label = "Total Payment",
                                value = "₱${"%,.2f".format(finalTotal)}",
                                isBold = true,
                                color = ElayaRoseDark
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // Checkout Bottom Sheet
    if (showCheckoutSheet) {
        var barangayDropdownExpanded by remember { mutableStateOf(false) }

        ModalBottomSheet(
            onDismissRequest = { showCheckoutSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Confirm Delivery & Payment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Select Barangay in Biñan (Complete 24 barangays)
                Text(text = "Delivery Barangay in Biñan (24 Barangays):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                ExposedDropdownMenuBox(
                    expanded = barangayDropdownExpanded,
                    onExpandedChange = { barangayDropdownExpanded = !barangayDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "$selectedBarangay (${barangayInfo.landmark})",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = barangayDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = barangayDropdownExpanded,
                        onDismissRequest = { barangayDropdownExpanded = false }
                    ) {
                        BinanLocations.allBarangays.forEach { b ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = b.name, fontWeight = FontWeight.Bold)
                                        Text(text = "${b.landmark} • ₱${b.deliveryFee.toInt()}", style = MaterialTheme.typography.bodySmall, color = ElayaTextSecondary)
                                    }
                                },
                                onClick = {
                                    selectedBarangay = b.name
                                    barangayDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = streetAddress,
                    onValueChange = { streetAddress = it },
                    label = { Text("House / Street / Unit / Subdivision") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = recipientPhone,
                    onValueChange = { recipientPhone = it },
                    label = { Text("Contact Phone (for delivery rider)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Payment Options
                Text(text = "Select Payment Method:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                val paymentMethods = listOf("GCash", "Maya", "Cash on Delivery", "Credit/Debit Card")
                paymentMethods.forEach { method ->
                    Surface(
                        onClick = { selectedPaymentMethod = method },
                        color = if (selectedPaymentMethod == method) ElayaPinkLight else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (selectedPaymentMethod == method) ElayaPinkPrimary else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == method,
                                    onClick = { selectedPaymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = ElayaRoseDark)
                                )
                                Text(text = method, fontWeight = FontWeight.SemiBold)
                            }
                            if (method == "GCash" || method == "Maya") {
                                Surface(
                                    color = Color(0xFF007DFE).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Instant Pay",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF007DFE),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Confirm Place Order
                Button(
                    onClick = {
                        val user = currentUser ?: return@Button
                        isPlacingOrder = true
                        coroutineScope.launch {
                            val orderId = "ELA-2026-${(100..999).random()}"
                            val primaryShopId = cartItems.firstOrNull()?.shopId ?: "shop-1"
                            val primaryShopName = cartItems.firstOrNull()?.shopName ?: "Bella Flora Biñan"
                            val productsSummary = cartItems.joinToString(", ") { "${it.quantity}x ${it.productName}" }

                            val newOrder = OrderEntity(
                                id = orderId,
                                customerId = user.id,
                                customerName = user.name,
                                customerPhone = recipientPhone,
                                shopId = primaryShopId,
                                shopName = primaryShopName,
                                productsSummary = productsSummary,
                                quantity = cartItems.sumOf { it.quantity },
                                totalPrice = finalTotal,
                                deliveryAddress = "$streetAddress, $selectedBarangay, Biñan, Laguna",
                                barangay = selectedBarangay,
                                orderDate = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).format(Date()),
                                paymentStatus = if (selectedPaymentMethod == "Cash on Delivery") "COD Pending" else "Paid ($selectedPaymentMethod)",
                                orderStatus = "Pending",
                                riderName = "Dennis Dela Cruz",
                                riderPhone = "0920-555-7890",
                                etaMinutes = barangayInfo.estMinutes
                            )

                            repository.placeOrder(newOrder)
                            isPlacingOrder = false
                            showCheckoutSheet = false
                            onOrderPlaced(orderId)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_order_button"),
                    enabled = !isPlacingOrder
                ) {
                    if (isPlacingOrder) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Text("Confirm & Place Order • ₱${"%,.2f".format(finalTotal)}", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItemEntity,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (item.isCustom3D) {
                            Surface(
                                color = ElayaPinkPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "3D Custom",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ElayaRoseDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = item.productName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElayaTextPrimary
                        )
                    }
                    Text(
                        text = "From: ${item.shopName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElayaTextSecondary
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = Color.Gray)
                }
            }

            // Specs if custom 3D
            if (item.isCustom3D) {
                Surface(
                    color = Color(0xFFFFF9FA),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Color: ${item.selectedColor} • Wrap: ${item.selectedWrapping} • Ribbon: ${item.selectedRibbon}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElayaTextSecondary
                        )
                        if (item.cardMessage.isNotBlank()) {
                            Text(
                                text = "Card: \"${item.cardMessage}\"",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElayaRoseDark,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // Price & Quantity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₱${"%,.2f".format(item.price * item.quantity)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElayaRoseDark
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(16.dp))
                    }
                    Text(text = "${item.quantity}", fontWeight = FontWeight.Bold)
                    FilledTonalIconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    color: Color = ElayaTextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) ElayaTextPrimary else ElayaTextSecondary
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = color
        )
    }
}
