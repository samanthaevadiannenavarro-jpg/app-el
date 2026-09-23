package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FlowerShopEntity
import com.example.data.repository.ElayaRepository
import com.example.ui.components.ElayaStatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminShopsScreen(
    repository: ElayaRepository
) {
    val coroutineScope = rememberCoroutineScope()
    val allShops by repository.getAllShops().collectAsStateWithLifecycle(emptyList())
    var showAddShopModal by remember { mutableStateOf(false) }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Partner Flower Shops",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AdminIndigoDark
                    )
                    Text(
                        text = "Registered florists operating in Biñan (${allShops.size} shops)",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElayaTextSecondary
                    )
                }

                Button(
                    onClick = { showAddShopModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminIndigoDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("admin_register_shop_btn")
                ) {
                    Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Add Shop")
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(allShops, key = { it.id }) { shop ->
                AdminShopCard(
                    shop = shop,
                    onToggleStatus = {
                        val newStatus = if (shop.status == "Active") "Suspended" else "Active"
                        coroutineScope.launch {
                            repository.updateShop(shop.copy(status = newStatus))
                        }
                    }
                )
            }
        }
    }

    // Add Shop Modal
    if (showAddShopModal) {
        var shopName by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("San Vicente, Biñan, Laguna") }
        var phone by remember { mutableStateOf("0917-000-1122") }
        var desc by remember { mutableStateOf("Artisan floral workshop") }

        ModalBottomSheet(
            onDismissRequest = { showAddShopModal = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Register Partner Florist",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AdminIndigoDark
                )

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Shop Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Biñan Location / Barangay") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Shop Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (shopName.isNotBlank()) {
                            coroutineScope.launch {
                                repository.createShop(
                                    FlowerShopEntity(
                                        id = "shop-${UUID.randomUUID().toString().take(6)}",
                                        ownerId = "owner-new",
                                        shopName = shopName,
                                        description = desc,
                                        contactInfo = phone,
                                        location = location,
                                        status = "Active"
                                    )
                                )
                                showAddShopModal = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminIndigoDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Register & Approve Shop", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AdminShopCard(
    shop: FlowerShopEntity,
    onToggleStatus: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_shop_card_${shop.id}")
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AdminIndigoLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = AdminIndigoDark)
                    }
                    Column {
                        Text(
                            text = shop.shopName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElayaTextPrimary
                        )
                        Text(
                            text = "Owner ID: ${shop.ownerId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElayaTextMuted
                        )
                    }
                }
                ElayaStatusBadge(status = shop.status)
            }

            Text(
                text = shop.description,
                style = MaterialTheme.typography.bodySmall,
                color = ElayaTextSecondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = AdminIndigoPrimary, modifier = Modifier.size(16.dp))
                Text(
                    text = shop.location,
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
                    text = "Rating: ${shop.rating} ★ • Phone: ${shop.contactInfo}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElayaTextSecondary
                )

                OutlinedButton(
                    onClick = onToggleStatus,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (shop.status == "Active") Color(0xFFDC2626) else StatusSuccess
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(if (shop.status == "Active") "Suspend" else "Activate")
                }
            }
        }
    }
}
