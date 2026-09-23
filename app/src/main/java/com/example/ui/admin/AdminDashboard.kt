package com.example.ui.admin

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
import com.example.data.model.UserRole
import com.example.data.repository.ElayaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminDashboard(
    repository: ElayaRepository,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0=Overview, 1=Shops, 2=Users, 3=Orders, 4=Settings

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
                        selectedIconColor = AdminIndigoDark,
                        selectedTextColor = AdminIndigoDark,
                        indicatorColor = AdminIndigoLight
                    ),
                    modifier = Modifier.testTag("nav_admin_overview")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Default.Store else Icons.Outlined.Store,
                            contentDescription = "Shops"
                        )
                    },
                    label = { Text("Shops") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AdminIndigoDark,
                        selectedTextColor = AdminIndigoDark,
                        indicatorColor = AdminIndigoLight
                    ),
                    modifier = Modifier.testTag("nav_admin_shops")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Default.People else Icons.Outlined.People,
                            contentDescription = "Users"
                        )
                    },
                    label = { Text("Users") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AdminIndigoDark,
                        selectedTextColor = AdminIndigoDark,
                        indicatorColor = AdminIndigoLight
                    ),
                    modifier = Modifier.testTag("nav_admin_users")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 3) Icons.Default.LocalShipping else Icons.Outlined.LocalShipping,
                            contentDescription = "Orders"
                        )
                    },
                    label = { Text("Orders") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AdminIndigoDark,
                        selectedTextColor = AdminIndigoDark,
                        indicatorColor = AdminIndigoLight
                    ),
                    modifier = Modifier.testTag("nav_admin_orders")
                )

                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 4) Icons.Default.Settings else Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AdminIndigoDark,
                        selectedTextColor = AdminIndigoDark,
                        indicatorColor = AdminIndigoLight
                    ),
                    modifier = Modifier.testTag("nav_admin_settings")
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
                0 -> AdminOverviewScreen(
                    repository = repository,
                    onNavigateToShops = { selectedTab = 1 },
                    onNavigateToUsers = { selectedTab = 2 },
                    onNavigateToOrders = { selectedTab = 3 }
                )
                1 -> AdminShopsScreen(
                    repository = repository
                )
                2 -> AdminUsersScreen(
                    repository = repository
                )
                3 -> AdminOrdersScreen(
                    repository = repository
                )
                4 -> AdminSettingsScreen(
                    repository = repository,
                    onLogout = onLogout,
                    onSwitchRole = onSwitchRole
                )
            }
        }
    }
}

@Composable
private fun AdminSettingsScreen(
    repository: ElayaRepository,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "System Administration Settings",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AdminIndigoDark
        )

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
                    text = "Platform Configuration",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Operating Region: City of Biñan, Province of Laguna",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElayaTextSecondary
                )
                Text(
                    text = "Active Barangays: 24 (San Vicente, Canlalay, Zapote, Casile, San Francisco...)",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElayaTextSecondary
                )
                Text(
                    text = "Base Courier Fee: ₱39.00 - ₱59.00 (Distance-calculated)",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElayaTextSecondary
                )
            }
        }

        // Fast Role Switcher
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
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Switch active session to test different dashboard role experiences:",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElayaTextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            Text("Customer", style = MaterialTheme.typography.labelSmall, color = ElayaPinkPrimary)
                        }
                    }

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
                .testTag("admin_logout_button")
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFB91C1C))
            Spacer(Modifier.width(8.dp))
            Text("Log Out", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold)
        }
    }
}
