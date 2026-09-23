package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.BinanLocations
import com.example.data.model.UserRole
import com.example.data.repository.ElayaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    repository: ElayaRepository,
    onLoginSuccess: (UserRole) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showAuthSheet by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }

    // Form inputs
    var email by remember { mutableStateOf("customer@elaya.ph") }
    var password by remember { mutableStateOf("password123") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var shopName by remember { mutableStateOf("") }
    var selectedBarangay by remember { mutableStateOf("San Vicente") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ElayaGradients.softBlush)
    ) {
        // Welcome Hero Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Branding & Icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(ElayaGradients.floralPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = "Elaya Floral Logo",
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Text(
                    text = "Welcome to Elaya!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ElayaRoseDark
                )

                Text(
                    text = "Artisan Flower Delivery & 3D Bouquet Studio in Biñan, Laguna",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = ElayaTextSecondary
                )
            }

            // Quick Role Preview Cards (Indicating multi-role features)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RolePreviewCard(
                    title = "Customer Portal",
                    desc = "Browse shops, customize 3D bouquets & track live rider GPS",
                    icon = Icons.Outlined.ShoppingBag,
                    color = ElayaPinkPrimary
                )
                RolePreviewCard(
                    title = "Flower Shop Owner",
                    desc = "Manage your shop, add flowers/bouquets/wraps & dispatch orders",
                    icon = Icons.Outlined.Storefront,
                    color = OwnerEmeraldPrimary
                )
                RolePreviewCard(
                    title = "Admin Portal",
                    desc = "Full marketplace control, user management & reports",
                    icon = Icons.Outlined.AdminPanelSettings,
                    color = AdminIndigoPrimary
                )
            }

            // Get Started Button
            Button(
                onClick = { showAuthSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("get_started_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Get Started / Sign In",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Shopee-style Bottom Sheet for Authentication
        if (showAuthSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showAuthSheet = false
                    errorMessage = null
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Bar
                    Text(
                        text = if (isSignUpMode) "Create an Elaya Account" else "Sign In to Elaya",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ElayaTextPrimary
                    )

                    // 1-Tap Demo Switcher (Crucial for test evaluation of roles)
                    Surface(
                        color = ElayaPinkLight,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⚡ Quick Demo Logins (1-Tap Test):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElayaRoseDark
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DemoChip(
                                    label = "Customer",
                                    icon = Icons.Default.Person,
                                    color = ElayaPinkPrimary,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    email = "customer@elaya.ph"
                                    password = "password123"
                                    isSignUpMode = false
                                }
                                DemoChip(
                                    label = "Shop Owner",
                                    icon = Icons.Default.Store,
                                    color = OwnerEmeraldPrimary,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    email = "owner@roselaguna.ph"
                                    password = "password123"
                                    isSignUpMode = false
                                }
                                DemoChip(
                                    label = "Admin",
                                    icon = Icons.Default.Security,
                                    color = AdminIndigoPrimary,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    email = "admin@elaya.ph"
                                    password = "password123"
                                    isSignUpMode = false
                                }
                            }
                        }
                    }

                    // Error Message
                    if (errorMessage != null) {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = Color(0xFFB91C1C),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Sign-Up Specific Fields
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Role Selector
                        Text(
                            text = "Choose Account Role:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = ElayaTextSecondary,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedRole == UserRole.CUSTOMER,
                                onClick = { selectedRole = UserRole.CUSTOMER },
                                label = { Text("Customer") },
                                leadingIcon = { Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = selectedRole == UserRole.FLOWER_OWNER,
                                onClick = { selectedRole = UserRole.FLOWER_OWNER },
                                label = { Text("Shop Owner") },
                                leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (selectedRole == UserRole.FLOWER_OWNER) {
                            OutlinedTextField(
                                value = shopName,
                                onValueChange = { shopName = it },
                                label = { Text("Flower Shop Name") },
                                placeholder = { Text("e.g. Laguna Floral Haven") },
                                leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Barangay Selector
                        var barangayMenuExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = barangayMenuExpanded,
                            onExpandedChange = { barangayMenuExpanded = !barangayMenuExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedBarangay,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Barangay in Biñan, Laguna") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = barangayMenuExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = barangayMenuExpanded,
                                onDismissRequest = { barangayMenuExpanded = false }
                            ) {
                                BinanLocations.allBarangays.forEach { brgy ->
                                    DropdownMenuItem(
                                        text = { Text(brgy.name) },
                                        onClick = {
                                            selectedBarangay = brgy.name
                                            barangayMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Forgot Password (if in login mode)
                    if (!isSignUpMode) {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElayaRoseDark,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable { /* Password reset guidance */ }
                        )
                    }

                    // Submit Action Button
                    Button(
                        onClick = {
                            errorMessage = null
                            isLoading = true
                            coroutineScope.launch {
                                if (isSignUpMode) {
                                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                                        errorMessage = "Please fill in all required fields"
                                        isLoading = false
                                        return@launch
                                    }
                                    val regResult = repository.registerUser(
                                        name = name,
                                        email = email,
                                        password = password,
                                        role = selectedRole,
                                        phone = phone,
                                        barangay = selectedBarangay,
                                        address = "$selectedBarangay, Biñan, Laguna",
                                        shopName = shopName
                                    )
                                    regResult.fold(
                                        onSuccess = { user ->
                                            isLoading = false
                                            showAuthSheet = false
                                            onLoginSuccess(UserRole.fromRoleName(user.role))
                                        },
                                        onFailure = { err ->
                                            isLoading = false
                                            errorMessage = err.message ?: "Sign up failed"
                                        }
                                    )
                                } else {
                                    val loginResult = repository.login(email, password)
                                    loginResult.fold(
                                        onSuccess = { user ->
                                            isLoading = false
                                            showAuthSheet = false
                                            onLoginSuccess(UserRole.fromRoleName(user.role))
                                        },
                                        onFailure = { err ->
                                            isLoading = false
                                            errorMessage = err.message ?: "Invalid email or password"
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElayaRoseDark),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (isSignUpMode) "Create Account" else "Sign In",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Social login options
                    Text(
                        text = "Or continue with",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElayaTextMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                // Google Sign-in demo flow
                                coroutineScope.launch {
                                    repository.switchDemoAccount(UserRole.CUSTOMER)
                                    showAuthSheet = false
                                    onLoginSuccess(UserRole.CUSTOMER)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("google_signin_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF4285F4))
                            Spacer(Modifier.width(6.dp))
                            Text("Google", style = MaterialTheme.typography.bodyMedium)
                        }

                        OutlinedButton(
                            onClick = {
                                // Facebook Sign-in placeholder flow
                                coroutineScope.launch {
                                    repository.switchDemoAccount(UserRole.CUSTOMER)
                                    showAuthSheet = false
                                    onLoginSuccess(UserRole.CUSTOMER)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("facebook_signin_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Facebook, contentDescription = null, tint = Color(0xFF1877F2))
                            Spacer(Modifier.width(6.dp))
                            Text("Facebook", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    // Toggle Sign In / Sign Up
                    Row(
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUpMode) "Already have an account? " else "Don't have an account? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElayaTextSecondary
                        )
                        Text(
                            text = if (isSignUpMode) "Sign In" else "Register",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElayaRoseDark,
                            modifier = Modifier.clickable {
                                isSignUpMode = !isSignUpMode
                                errorMessage = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoChip(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun RolePreviewCard(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = ElayaTextSecondary)
            }
        }
    }
}
