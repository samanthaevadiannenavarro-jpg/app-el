package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.BinanLocations
import com.example.data.repository.ElayaRepository
import com.example.ui.components.ElayaStatCard
import com.example.ui.theme.*

@Composable
fun AdminOverviewScreen(
    repository: ElayaRepository,
    onNavigateToShops: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    val allShops by repository.getAllShops().collectAsStateWithLifecycle(emptyList())
    val allUsers by repository.getAllUsers().collectAsStateWithLifecycle(emptyList())
    val allOrders by repository.getAllOrders().collectAsStateWithLifecycle(emptyList())
    val allProducts by repository.getAllProducts().collectAsStateWithLifecycle(emptyList())

    val totalGMV = allOrders.sumOf { it.totalPrice }
    val activeShopsCount = allShops.count { it.status == "Active" }
    val totalCustomers = allUsers.count { it.role == "customer" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Admin Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AdminIndigoDark),
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
                        text = "ADMINISTRATOR SYSTEM DASHBOARD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Elaya Operations Control",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "Marketplace Governance & Biñan Dispatch Infrastructure",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // Stat Cards Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElayaStatCard(
                title = "Total Marketplace GMV",
                value = "₱${"%,.0f".format(totalGMV)}",
                icon = Icons.Default.MonetizationOn,
                iconColor = AdminIndigoPrimary,
                subtitle = "Across all Biñan vendors",
                modifier = Modifier.weight(1f)
            )

            ElayaStatCard(
                title = "Total Orders",
                value = "${allOrders.size}",
                icon = Icons.Default.ReceiptLong,
                iconColor = ElayaRoseDark,
                subtitle = "Completed & In-transit",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElayaStatCard(
                title = "Active Florists",
                value = "$activeShopsCount / ${allShops.size}",
                icon = Icons.Default.Storefront,
                iconColor = OwnerEmeraldDark,
                subtitle = "Partner shops in Biñan",
                modifier = Modifier.weight(1f)
            )

            ElayaStatCard(
                title = "Registered Users",
                value = "${allUsers.size}",
                icon = Icons.Default.People,
                iconColor = Color(0xFF6366F1),
                subtitle = "$totalCustomers Customers",
                modifier = Modifier.weight(1f)
            )
        }

        // System Health & Coverage
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
                Text(
                    text = "Biñan City Infrastructure Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Coverage Barangays", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${BinanLocations.allBarangays.size} / 24 Barangays Active",
                        fontWeight = FontWeight.Bold,
                        color = StatusSuccess
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "GPS Dispatch System", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Online (Normal Latency)",
                        fontWeight = FontWeight.Bold,
                        color = StatusSuccess
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Live Catalog Products", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${allProducts.size} Items Listed",
                        fontWeight = FontWeight.Bold,
                        color = ElayaTextPrimary
                    )
                }
            }
        }

        // Quick Navigation Buttons
        Text(
            text = "Administration Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ElayaTextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onNavigateToShops,
                colors = ButtonDefaults.buttonColors(containerColor = AdminIndigoDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Manage Shops")
            }

            Button(
                onClick = onNavigateToUsers,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Manage Users")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
