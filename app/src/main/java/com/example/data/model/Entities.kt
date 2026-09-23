package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val roleName: String, val displayName: String) {
    CUSTOMER("customer", "Customer"),
    FLOWER_OWNER("flower_owner", "Flower Shop Owner"),
    ADMIN("admin", "Admin");

    companion object {
        fun fromRoleName(roleName: String): UserRole {
            return entries.find { it.roleName.equals(roleName, ignoreCase = true) } ?: CUSTOMER
        }
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val password: String = "password123",
    val role: String, // "customer", "flower_owner", "admin"
    val status: String = "active", // "active", "suspended"
    val phone: String = "0917-123-4567",
    val defaultBarangay: String = "San Vicente",
    val defaultAddress: String = "Blk 12 Lot 4, San Vicente, Biñan, Laguna",
    val registeredDate: String = "2026-01-15",
    val avatarUrl: String = ""
)

@Entity(tableName = "flower_shops")
data class FlowerShopEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val shopName: String,
    val description: String,
    val location: String, // e.g. "San Vicente, Biñan, Laguna"
    val barangay: String = "San Vicente",
    val contactInfo: String = "0918-888-9999",
    val rating: Double = 4.9,
    val reviewCount: Int = 128,
    val status: String = "active", // "active", "pending", "suspended"
    val bannerUrl: String = "",
    val openingHours: String = "8:00 AM - 8:00 PM"
)

enum class ProductType(val typeName: String, val displayName: String) {
    FLOWER("flower", "Single Flower"),
    BOUQUET("bouquet", "Bouquet"),
    WRAPPING("wrapping", "Wrapping Option");

    companion object {
        fun fromTypeName(type: String): ProductType {
            return entries.find { it.typeName.equals(type, ignoreCase = true) } ?: FLOWER
        }
    }
}

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val shopId: String,
    val ownerId: String,
    val productName: String,
    val productType: String, // "flower", "bouquet", "wrapping"
    val category: String, // "Roses", "Sunflowers", "Tulips", "Romantic", "Sympathy", "Wrapping"
    val description: String,
    val imageUrl: String,
    val price: Double,
    val stock: Int,
    val status: String = "Available", // "Available", "Out of Stock", "Archived"
    val availableColors: String = "Red,Pink,White",
    // Specific bouquet fields
    val flowersIncluded: String = "",
    val numberOfFlowers: Int = 12,
    val wrapping: String = "White Wrapper",
    val ribbon: String = "Red Ribbon",
    // Specific wrapping fields
    val wrapColorStyle: String = ""
)

enum class OrderStatus(val label: String, val stepIndex: Int) {
    PENDING("Pending", 0),
    CONFIRMED("Confirmed", 1),
    PREPARING("Preparing", 2),
    READY_FOR_DELIVERY("Ready for Delivery", 3),
    OUT_FOR_DELIVERY("Out for Delivery", 4),
    COMPLETED("Completed", 5);

    companion object {
        fun fromLabel(label: String): OrderStatus {
            return entries.find { it.label.equals(label, ignoreCase = true) } ?: PENDING
        }
    }
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String, // e.g. "ELA-2026-101"
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val shopId: String,
    val shopName: String,
    val productsSummary: String,
    val quantity: Int,
    val totalPrice: Double,
    val deliveryAddress: String,
    val barangay: String,
    val orderDate: String,
    val paymentStatus: String = "Paid (GCash)", // "Paid (GCash)", "Paid (Maya)", "Cash on Delivery"
    val orderStatus: String = "Pending",
    val riderName: String = "Dennis Dela Cruz",
    val riderPhone: String = "0920-555-7890",
    val riderVehicle: String = "Yamaha Mio (Plate: 4024-BL)",
    val etaMinutes: Int = 25,
    val dispatchNotes: String = "Handle with care - fresh blooms"
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val productId: String,
    val shopId: String,
    val shopName: String,
    val productName: String,
    val productType: String,
    val price: Double,
    val quantity: Int = 1,
    val selectedColor: String = "Pink",
    val selectedWrapping: String = "Pastel Pink Wrapper",
    val selectedRibbon: String = "Cream Silk Ribbon",
    val flowerCount: Int = 12,
    val cardMessage: String = "",
    val isCustom3D: Boolean = false,
    val imageUrl: String = ""
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val productId: String
)
