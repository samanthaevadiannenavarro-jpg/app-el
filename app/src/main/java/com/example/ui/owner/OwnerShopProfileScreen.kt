package com.example.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.data.model.UserRole
import com.example.data.repository.ElayaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerShopProfileScreen(
    repository: ElayaRepository,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val currentShop by repository.currentShop.collectAsStateWithLifecycle()

    var showEditShopModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shop Profile Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(OwnerEmeraldDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Text(
                    text = currentShop?.shopName ?: "Bella Flora Biñan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElayaTextPrimary
                )

                Surface(
                    color = OwnerEmeraldLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Verified Flower Merchant • Biñan, Laguna",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = OwnerEmeraldDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = currentShop?.description ?: "Premier artisan florist providing handcrafted floral arrangements.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElayaTextSecondary
                )
            }
        }

        // Shop Contact & Location Details
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Flower Shop Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { showEditShopModal = true }) {
                        Text("Edit", color = OwnerEmeraldDark)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = OwnerEmeraldDark)
                    Column {
                        Text(text = "Location", style = MaterialTheme.typography.labelSmall, color = ElayaTextSecondary)
                        Text(text = currentShop?.location ?: "San Vicente, Biñan, Laguna", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = OwnerEmeraldDark)
                    Column {
                        Text(text = "Contact Phone", style = MaterialTheme.typography.labelSmall, color = ElayaTextSecondary)
                        Text(text = currentShop?.contactInfo ?: "0917-888-1234", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = OwnerEmeraldDark)
                    Column {
                        Text(text = "Operating Hours", style = MaterialTheme.typography.labelSmall, color = ElayaTextSecondary)
                        Text(text = "Mon - Sun: 7:00 AM - 9:00 PM", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Fast Role Switcher (For testing multi-role requirement)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Role Switcher / Test Accounts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElayaTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.switchDemoAccount(UserRole.CUSTOMER)
                                onSwitchRole(UserRole.CUSTOMER)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = ElayaPinkPrimary)
                            Text("Customer View", style = MaterialTheme.typography.labelSmall, color = ElayaPinkPrimary)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.switchDemoAccount(UserRole.ADMIN)
                                onSwitchRole(UserRole.ADMIN)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = AdminIndigoPrimary)
                            Text("Admin Portal", style = MaterialTheme.typography.labelSmall, color = AdminIndigoPrimary)
                        }
                    }
                }
            }
        }

        // Logout
        Button(
            onClick = {
                repository.logout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("owner_logout_button")
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFB91C1C))
            Spacer(Modifier.width(8.dp))
            Text("Log Out", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Edit Shop Info Modal
    if (showEditShopModal && currentShop != null) {
        val shop = currentShop!!
        var editName by remember { mutableStateOf(shop.shopName) }
        var editDesc by remember { mutableStateOf(shop.description) }
        var editPhone by remember { mutableStateOf(shop.contactInfo) }
        var editLocation by remember { mutableStateOf(shop.location) }

        ModalBottomSheet(
            onDismissRequest = { showEditShopModal = false },
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
                    text = "Edit Shop Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OwnerEmeraldDark
                )

                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Flower Shop Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editLocation,
                    onValueChange = { editLocation = it },
                    label = { Text("Location in Biñan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editPhone,
                    onValueChange = { editPhone = it },
                    label = { Text("Contact Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editDesc,
                    onValueChange = { editDesc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            repository.updateShop(
                                shop.copy(
                                    shopName = editName,
                                    description = editDesc,
                                    contactInfo = editPhone,
                                    location = editLocation
                                )
                            )
                            showEditShopModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OwnerEmeraldDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Save Shop Changes", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
