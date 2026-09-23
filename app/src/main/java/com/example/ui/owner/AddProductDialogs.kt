package com.example.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.ProductEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlowerDialog(
    onDismiss: () -> Unit,
    onAddFlower: (name: String, category: String, desc: String, image: String, colors: String, price: Double, stock: Int, availability: String) -> Unit
) {
    var flowerName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Roses") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1518895949257-7621c3c786d7?w=600") }
    var availableColors by remember { mutableStateOf("Red, Pink, White") }
    var priceText by remember { mutableStateOf("50") }
    var stockText by remember { mutableStateOf("100") }
    var availability by remember { mutableStateOf("Available") }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Flower Product",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OwnerEmeraldDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            if (error != null) {
                Text(text = error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = flowerName,
                onValueChange = { flowerName = it },
                label = { Text("Flower Name *") },
                placeholder = { Text("e.g. Red Rose") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("add_flower_name_input"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Flower Category") },
                placeholder = { Text("e.g. Roses, Tulips, Sunflowers") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Flower Description") },
                placeholder = { Text("Fresh-cut long stem roses from flower farm...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Flower Image URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = availableColors,
                onValueChange = { availableColors = it },
                label = { Text("Available Colors (comma-separated)") },
                placeholder = { Text("e.g. Red, Pink, White") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price (₱) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("add_flower_price_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock Quantity *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("add_flower_stock_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Availability
            Text(text = "Availability Status:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Available", "Out of Stock").forEach { opt ->
                    FilterChip(
                        selected = availability == opt,
                        onClick = { availability = opt },
                        label = { Text(opt) }
                    )
                }
            }

            Button(
                onClick = {
                    val p = priceText.toDoubleOrNull()
                    val s = stockText.toIntOrNull()
                    if (flowerName.isBlank() || p == null || s == null) {
                        error = "Please provide valid flower name, price, and stock"
                        return@Button
                    }
                    onAddFlower(flowerName, category, description, imageUrl, availableColors, p, s, availability)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OwnerEmeraldDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_add_flower_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Flower", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBouquetDialog(
    onDismiss: () -> Unit,
    onAddBouquet: (name: String, image: String, desc: String, flowersInc: String, count: Int, wrap: String, ribbon: String, price: Double, stock: Int, availability: String) -> Unit
) {
    var bouquetName by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1561181286-d3fee7d55364?w=600") }
    var description by remember { mutableStateOf("") }
    var flowersIncluded by remember { mutableStateOf("Red Roses") }
    var numberOfFlowersText by remember { mutableStateOf("12") }
    var wrapping by remember { mutableStateOf("White Wrapper") }
    var ribbon by remember { mutableStateOf("Red Ribbon") }
    var priceText by remember { mutableStateOf("850") }
    var stockText by remember { mutableStateOf("10") }
    var availability by remember { mutableStateOf("Available") }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Ready Bouquet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OwnerEmeraldDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            if (error != null) {
                Text(text = error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = bouquetName,
                onValueChange = { bouquetName = it },
                label = { Text("Bouquet Name *") },
                placeholder = { Text("e.g. Romantic Red Roses") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("add_bouquet_name_input"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = flowersIncluded,
                onValueChange = { flowersIncluded = it },
                label = { Text("Flowers Included *") },
                placeholder = { Text("e.g. Red Roses, Baby's Breath") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = numberOfFlowersText,
                    onValueChange = { numberOfFlowersText = it },
                    label = { Text("No. of Flowers") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = wrapping,
                    onValueChange = { wrapping = it },
                    label = { Text("Wrapping Style") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = ribbon,
                onValueChange = { ribbon = it },
                label = { Text("Ribbon") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Bouquet Image URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price (₱) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("add_bouquet_price_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock Quantity *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("add_bouquet_stock_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Button(
                onClick = {
                    val p = priceText.toDoubleOrNull()
                    val s = stockText.toIntOrNull()
                    val count = numberOfFlowersText.toIntOrNull() ?: 12
                    if (bouquetName.isBlank() || p == null || s == null) {
                        error = "Please provide valid bouquet name, price, and stock"
                        return@Button
                    }
                    onAddBouquet(bouquetName, imageUrl, description, flowersIncluded, count, wrapping, ribbon, p, s, availability)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OwnerEmeraldDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_add_bouquet_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Bouquet", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWrappingDialog(
    onDismiss: () -> Unit,
    onAddWrapping: (name: String, image: String, colorStyle: String, price: Double, stock: Int, availability: String) -> Unit
) {
    var wrapName by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=600") }
    var colorStyle by remember { mutableStateOf("Matte Finish") }
    var priceText by remember { mutableStateOf("45") }
    var stockText by remember { mutableStateOf("150") }
    var availability by remember { mutableStateOf("Available") }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Wrapping Paper Option",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OwnerEmeraldDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "These wrapping options become selectable by customers in the 3D Bouquet Studio!",
                style = MaterialTheme.typography.bodySmall,
                color = OwnerEmeraldPrimary
            )

            if (error != null) {
                Text(text = error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = wrapName,
                onValueChange = { wrapName = it },
                label = { Text("Wrap Name *") },
                placeholder = { Text("e.g. Kraft Paper, Pink Wrapper, White Wrapper") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("add_wrap_name_input"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = colorStyle,
                onValueChange = { colorStyle = it },
                label = { Text("Wrap Color / Style *") },
                placeholder = { Text("e.g. Natural Rustic Brown, Matte Blush Pink") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Wrap Image URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price (₱) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("add_wrap_price_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("add_wrap_stock_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Button(
                onClick = {
                    val p = priceText.toDoubleOrNull()
                    val s = stockText.toIntOrNull()
                    if (wrapName.isBlank() || p == null || s == null) {
                        error = "Please provide valid wrapping name, price, and stock"
                        return@Button
                    }
                    onAddWrapping(wrapName, imageUrl, colorStyle, p, s, availability)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OwnerEmeraldDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_add_wrapping_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Wrapping", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(product.productName) }
    var priceText by remember { mutableStateOf(product.price.toString()) }
    var stockText by remember { mutableStateOf(product.stock.toString()) }
    var status by remember { mutableStateOf(product.status) }
    var description by remember { mutableStateOf(product.description) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Edit Product",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OwnerEmeraldDark
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price (₱)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Status Picker
            Text(text = "Status:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Available", "Out of Stock", "Archived").forEach { st ->
                    FilterChip(
                        selected = status == st,
                        onClick = { status = st },
                        label = { Text(st) }
                    )
                }
            }

            Button(
                onClick = {
                    val p = priceText.toDoubleOrNull() ?: product.price
                    val s = stockText.toIntOrNull() ?: product.stock
                    onSave(
                        product.copy(
                            productName = name,
                            price = p,
                            stock = s,
                            status = status,
                            description = description
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = OwnerEmeraldDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
