package com.example.ui.customer

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
import com.example.data.model.BinanLocations
import com.example.data.model.UserRole
import com.example.data.repository.ElayaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileScreen(
    repository: ElayaRepository,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    var showAddressEditor by remember { mutableStateOf(false) }

    val user = currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Header Card
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
                        .background(ElayaGradients.floralPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.name?.take(2)?.uppercase() ?: "CU",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = user?.name ?: "Valued Customer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ElayaTextPrimary
                )

                Text(
                    text = user?.email ?: "customer@elaya.ph",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElayaTextSecondary
                )

                Surface(
                    color = ElayaPinkLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = ElayaRoseDark, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Customer Account • Biñan, Laguna",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElayaRoseDark
                        )
                    }
                }
            }
        }

        // Delivery Location Details Card
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Default Delivery Address",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { showAddressEditor = !showAddressEditor }) {
                        Text(if (showAddressEditor) "Close" else "Edit")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = ElayaRoseDark)
                    Column {
                        Text(
                            text = "${user?.defaultBarangay ?: "San Vicente"}, Biñan",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user?.defaultAddress ?: "San Vicente, Biñan, Laguna",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElayaTextSecondary
                        )
                    }
                }

                if (showAddressEditor) {
                    var selectedBrgy by remember { mutableStateOf(user?.defaultBarangay ?: "San Vicente") }
                    var addressText by remember { mutableStateOf(user?.defaultAddress ?: "") }
                    var expandedBrgy by remember { mutableStateOf(false) }

                    Divider(color = ElayaBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Text(text = "Change Barangay (24 Biñan Barangays):", style = MaterialTheme.typography.labelSmall)
                    ExposedDropdownMenuBox(
                        expanded = expandedBrgy,
                        onExpandedChange = { expandedBrgy = !expandedBrgy },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedBrgy,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBrgy) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBrgy,
                            onDismissRequest = { expandedBrgy = false }
                        ) {
                            BinanLocations.allBarangays.forEach { brgy ->
                                DropdownMenuItem(
                                    text = { Text(brgy.name) },
                                    onClick = {
                                        selectedBrgy = brgy.name
                                        expandedBrgy = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = addressText,
                        onValueChange = { addressText = it },
                        label = { Text("House / Street") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            if (user != null) {
                                coroutineScope.launch {
                                    // Address update in user record
                                    showAddressEditor = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Delivery Address")
                    }
                }
            }
        }

        // Fast Role Switcher (Role-Based Demonstration Feature)
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
                Text(
                    text = "Test Elaya's three distinct role dashboards:",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElayaTextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.switchDemoAccount(UserRole.FLOWER_OWNER)
                                onSwitchRole(UserRole.FLOWER_OWNER)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = OwnerEmeraldPrimary)
                            Text("Shop Owner", style = MaterialTheme.typography.labelSmall, color = OwnerEmeraldPrimary)
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

        // Logout Button
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
                .testTag("customer_logout_button")
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFB91C1C))
            Spacer(Modifier.width(8.dp))
            Text("Log Out", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
