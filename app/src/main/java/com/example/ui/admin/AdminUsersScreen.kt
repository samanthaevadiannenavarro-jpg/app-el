package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.ElayaRepository
import com.example.ui.components.ElayaStatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminUsersScreen(
    repository: ElayaRepository
) {
    val coroutineScope = rememberCoroutineScope()
    val allUsers by repository.getAllUsers().collectAsStateWithLifecycle(emptyList())

    var selectedRoleFilter by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredUsers = remember(allUsers, selectedRoleFilter, searchQuery) {
        allUsers.filter { user ->
            val matchRole = selectedRoleFilter == "all" || user.role.equals(selectedRoleFilter, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() ||
                    user.name.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true)
            matchRole && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaBackground)
    ) {
        // Header & Filters
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
                    text = "System User Management",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AdminIndigoDark
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search users by name or email...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Pair("all", "All Users (${allUsers.size})"),
                        Pair("customer", "Customers (${allUsers.count { it.role == "customer" }})"),
                        Pair("flower_owner", "Shop Owners (${allUsers.count { it.role == "flower_owner" }})"),
                        Pair("admin", "Admins (${allUsers.count { it.role == "admin" }})")
                    ).forEach { (roleKey, label) ->
                        FilterChip(
                            selected = selectedRoleFilter == roleKey,
                            onClick = { selectedRoleFilter = roleKey },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }

        // Users List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredUsers, key = { it.id }) { user ->
                AdminUserCard(
                    user = user,
                    onToggleStatus = {
                        val newStatus = if (user.status == "Active") "Suspended" else "Active"
                        coroutineScope.launch {
                            repository.updateUser(user.copy(status = newStatus))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminUserCard(
    user: UserEntity,
    onToggleStatus: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_user_card_${user.id}")
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when (user.role) {
                                    "admin" -> AdminIndigoLight
                                    "flower_owner" -> OwnerEmeraldLight
                                    else -> ElayaPinkLight
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (user.role) {
                                "admin" -> Icons.Default.Security
                                "flower_owner" -> Icons.Default.Storefront
                                else -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = when (user.role) {
                                "admin" -> AdminIndigoDark
                                "flower_owner" -> OwnerEmeraldDark
                                else -> ElayaRoseDark
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(text = user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = ElayaTextSecondary)
                    }
                }

                ElayaStatusBadge(status = user.status)
            }

            Divider(color = ElayaBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Role: ${user.role.uppercase()} • Barangay: ${user.defaultBarangay}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElayaTextSecondary
                    )
                    Text(
                        text = "Phone: ${user.phone.ifBlank { "N/A" }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElayaTextMuted
                    )
                }

                OutlinedButton(
                    onClick = onToggleStatus,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (user.status == "Active") Color(0xFFDC2626) else StatusSuccess
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(if (user.status == "Active") "Suspend" else "Activate")
                }
            }
        }
    }
}
