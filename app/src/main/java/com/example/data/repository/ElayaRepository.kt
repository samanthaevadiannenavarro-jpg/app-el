package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ElayaRepository(
    private val database: ElayaDatabase,
    private val appScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    private val userDao = database.userDao()
    private val shopDao = database.flowerShopDao()
    private val productDao = database.productDao()
    private val orderDao = database.orderDao()
    private val cartItemDao = database.cartItemDao()
    private val favoriteDao = database.favoriteDao()

    // Current Session State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentShop = MutableStateFlow<FlowerShopEntity?>(null)
    val currentShop: StateFlow<FlowerShopEntity?> = _currentShop.asStateFlow()

    init {
        // Automatically attach default customer session on startup if none set
        appScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByEmail("customer@elaya.ph")
            if (user != null && _currentUser.value == null) {
                _currentUser.value = user
            }
        }
    }

    // -------------------------------------------------------------
    // AUTHENTICATION & ROLE MANAGEMENT
    // -------------------------------------------------------------
    suspend fun login(email: String, pass: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email.trim())
        if (user == null) {
            return@withContext Result.failure(Exception("Account not found with email: $email"))
        }
        if (user.password != pass.trim()) {
            return@withContext Result.failure(Exception("Incorrect password for account"))
        }
        if (user.status.equals("suspended", ignoreCase = true)) {
            return@withContext Result.failure(Exception("This account is suspended. Contact Elaya admin."))
        }
        _currentUser.value = user
        if (user.role == UserRole.FLOWER_OWNER.roleName) {
            val shop = shopDao.getShopByOwnerIdDirect(user.id)
            _currentShop.value = shop
        } else {
            _currentShop.value = null
        }
        Result.success(user)
    }

    suspend fun switchDemoAccount(role: UserRole) = withContext(Dispatchers.IO) {
        val targetEmail = when (role) {
            UserRole.CUSTOMER -> "customer@elaya.ph"
            UserRole.FLOWER_OWNER -> "owner@roselaguna.ph"
            UserRole.ADMIN -> "admin@elaya.ph"
        }
        val user = userDao.getUserByEmail(targetEmail)
        if (user != null) {
            _currentUser.value = user
            if (user.role == UserRole.FLOWER_OWNER.roleName) {
                _currentShop.value = shopDao.getShopByOwnerIdDirect(user.id)
            } else {
                _currentShop.value = null
            }
        }
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        phone: String,
        barangay: String,
        address: String,
        shopName: String? = null,
        shopDescription: String? = null
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = userDao.getUserByEmail(email.trim())
        if (existing != null) {
            return@withContext Result.failure(Exception("An account with this email already exists"))
        }
        val newUserId = "user-${UUID.randomUUID().toString().take(8)}"
        val newUser = UserEntity(
            id = newUserId,
            name = name.trim(),
            email = email.trim(),
            password = password.trim(),
            role = role.roleName,
            status = "Active",
            phone = phone.ifBlank { "0917-000-0000" },
            defaultBarangay = barangay,
            defaultAddress = address.ifBlank { "$barangay, Biñan, Laguna" },
            registeredDate = "2026-09-23"
        )
        userDao.insertUser(newUser)

        // If registered as Flower Shop Owner, automatically create shop entry
        if (role == UserRole.FLOWER_OWNER) {
            val newShopId = "shop-${UUID.randomUUID().toString().take(8)}"
            val newShop = FlowerShopEntity(
                id = newShopId,
                ownerId = newUserId,
                shopName = shopName?.ifBlank { "$name's Flower Boutique" } ?: "$name's Flower Boutique",
                description = shopDescription?.ifBlank { "Handcrafted florals in $barangay, Biñan." } ?: "Handcrafted florals in $barangay, Biñan.",
                location = "$address, $barangay, Biñan, Laguna",
                barangay = barangay,
                contactInfo = phone,
                status = "Active"
            )
            shopDao.insertShop(newShop)
            _currentShop.value = newShop
        }

        _currentUser.value = newUser
        Result.success(newUser)
    }

    fun logout() {
        _currentUser.value = null
        _currentShop.value = null
    }

    // -------------------------------------------------------------
    // CUSTOMER / MARKETPLACE
    // -------------------------------------------------------------
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    fun getProductsByType(type: String): Flow<List<ProductEntity>> = productDao.getProductsByType(type)

    fun getAllShops(): Flow<List<FlowerShopEntity>> = shopDao.getAllShops()

    fun getCart(customerId: String): Flow<List<CartItemEntity>> = cartItemDao.getCartItems(customerId)

    suspend fun addToCart(item: CartItemEntity) = withContext(Dispatchers.IO) {
        cartItemDao.insertCartItem(item)
    }

    suspend fun removeFromCart(itemId: String) = withContext(Dispatchers.IO) {
        cartItemDao.deleteCartItem(itemId)
    }

    suspend fun clearCart(customerId: String) = withContext(Dispatchers.IO) {
        cartItemDao.clearCart(customerId)
    }

    fun getFavoriteIds(customerId: String): Flow<List<String>> = favoriteDao.getFavoriteProductIds(customerId)

    suspend fun toggleFavorite(customerId: String, productId: String) = withContext(Dispatchers.IO) {
        val isFav = favoriteDao.isFavorite(customerId, productId)
        if (isFav) {
            favoriteDao.removeFavorite(customerId, productId)
        } else {
            favoriteDao.addFavorite(
                FavoriteEntity(
                    id = "fav-${UUID.randomUUID().toString().take(8)}",
                    customerId = customerId,
                    productId = productId
                )
            )
        }
    }

    suspend fun placeOrder(order: OrderEntity) = withContext(Dispatchers.IO) {
        orderDao.insertOrder(order)
        cartItemDao.clearCart(order.customerId)
    }

    fun getCustomerOrders(customerId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByCustomer(customerId)

    suspend fun getOrderById(orderId: String): OrderEntity? = withContext(Dispatchers.IO) {
        orderDao.getOrderById(orderId)
    }

    // -------------------------------------------------------------
    // FLOWER SHOP OWNER
    // -------------------------------------------------------------
    fun getProductsByOwner(ownerId: String): Flow<List<ProductEntity>> = productDao.getProductsByOwner(ownerId)

    fun getShopByOwner(ownerId: String): Flow<FlowerShopEntity?> = shopDao.getShopByOwnerId(ownerId)

    suspend fun addFlowerProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun addBouquetProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun addWrappingProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteOwnerProduct(productId: String, ownerId: String): Boolean = withContext(Dispatchers.IO) {
        val affected = productDao.deleteProductForOwner(productId, ownerId)
        affected > 0
    }

    fun getShopOrders(shopId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByShop(shopId)

    suspend fun updateOrder(order: OrderEntity) = withContext(Dispatchers.IO) {
        orderDao.updateOrder(order)
    }

    suspend fun updateOrderStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
    }

    suspend fun dispatchOrder(
        orderId: String,
        riderName: String,
        riderPhone: String,
        riderVehicle: String,
        status: String
    ) = withContext(Dispatchers.IO) {
        orderDao.dispatchOrder(orderId, riderName, riderPhone, riderVehicle, status)
    }

    suspend fun updateShop(shop: FlowerShopEntity) = withContext(Dispatchers.IO) {
        shopDao.updateShop(shop)
        _currentShop.value = shop
    }

    suspend fun createShop(shop: FlowerShopEntity) = withContext(Dispatchers.IO) {
        shopDao.insertShop(shop)
    }

    // -------------------------------------------------------------
    // ADMIN OPERATIONS
    // -------------------------------------------------------------
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    suspend fun updateUserStatus(userId: String, status: String) = withContext(Dispatchers.IO) {
        userDao.updateUserStatus(userId, status)
    }

    suspend fun updateUserRole(userId: String, role: String) = withContext(Dispatchers.IO) {
        userDao.updateUserRole(userId, role)
    }

    suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        userDao.deleteUser(userId)
    }

    suspend fun updateShopStatus(shopId: String, status: String) = withContext(Dispatchers.IO) {
        shopDao.updateShopStatus(shopId, status)
    }

    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun deleteProductAdmin(productId: String) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(productId)
    }
}
