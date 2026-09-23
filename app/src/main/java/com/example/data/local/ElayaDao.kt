package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, status: String)

    @Query("UPDATE users SET role = :role WHERE id = :userId")
    suspend fun updateUserRole(userId: String, role: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
}

@Dao
interface FlowerShopDao {
    @Query("SELECT * FROM flower_shops")
    fun getAllShops(): Flow<List<FlowerShopEntity>>

    @Query("SELECT * FROM flower_shops WHERE id = :shopId")
    suspend fun getShopById(shopId: String): FlowerShopEntity?

    @Query("SELECT * FROM flower_shops WHERE ownerId = :ownerId LIMIT 1")
    fun getShopByOwnerId(ownerId: String): Flow<FlowerShopEntity?>

    @Query("SELECT * FROM flower_shops WHERE ownerId = :ownerId LIMIT 1")
    suspend fun getShopByOwnerIdDirect(ownerId: String): FlowerShopEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: FlowerShopEntity)

    @Update
    suspend fun updateShop(shop: FlowerShopEntity)

    @Query("UPDATE flower_shops SET status = :status WHERE id = :shopId")
    suspend fun updateShopStatus(shopId: String, status: String)

    @Query("DELETE FROM flower_shops WHERE id = :shopId")
    suspend fun deleteShop(shopId: String)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE shopId = :shopId ORDER BY id DESC")
    fun getProductsByShop(shopId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE ownerId = :ownerId ORDER BY id DESC")
    fun getProductsByOwner(ownerId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE productType = :type ORDER BY id DESC")
    fun getProductsByType(type: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("DELETE FROM products WHERE id = :productId AND ownerId = :ownerId")
    suspend fun deleteProductForOwner(productId: String, ownerId: String): Int
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY orderDate DESC")
    fun getOrdersByCustomer(customerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE shopId = :shopId ORDER BY orderDate DESC")
    fun getOrdersByShop(shopId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Query("UPDATE orders SET riderName = :riderName, riderPhone = :riderPhone, riderVehicle = :vehicle, orderStatus = :status WHERE id = :orderId")
    suspend fun dispatchOrder(orderId: String, riderName: String, riderPhone: String, vehicle: String, status: String)
}

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_items WHERE customerId = :customerId")
    fun getCartItems(customerId: String): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun deleteCartItem(itemId: String)

    @Query("DELETE FROM cart_items WHERE customerId = :customerId")
    suspend fun clearCart(customerId: String)
}

@Dao
interface FavoriteDao {
    @Query("SELECT productId FROM favorites WHERE customerId = :customerId")
    fun getFavoriteProductIds(customerId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE customerId = :customerId AND productId = :productId")
    suspend fun removeFavorite(customerId: String, productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE customerId = :customerId AND productId = :productId)")
    suspend fun isFavorite(customerId: String, productId: String): Boolean
}
