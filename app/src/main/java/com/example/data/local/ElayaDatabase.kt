package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        FlowerShopEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        CartItemEntity::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ElayaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun flowerShopDao(): FlowerShopDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: ElayaDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        ): ElayaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ElayaDatabase::class.java,
                    "elaya_marketplace.db"
                )
                    .addCallback(ElayaDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ElayaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: ElayaDatabase) {
            val userDao = db.userDao()
            val shopDao = db.flowerShopDao()
            val productDao = db.productDao()
            val orderDao = db.orderDao()

            // 1. Users
            val initialUsers = listOf(
                UserEntity(
                    id = "cust-1",
                    name = "Sofia Andres",
                    email = "customer@elaya.ph",
                    password = "password123",
                    role = "customer",
                    status = "active",
                    phone = "0917-234-5678",
                    defaultBarangay = "San Vicente",
                    defaultAddress = "Blk 14 Lot 8, San Vicente, Biñan, Laguna",
                    registeredDate = "2026-01-10"
                ),
                UserEntity(
                    id = "owner-1",
                    name = "Elena Ramos",
                    email = "owner@roselaguna.ph",
                    password = "password123",
                    role = "flower_owner",
                    status = "active",
                    phone = "0918-777-1234",
                    defaultBarangay = "San Vicente",
                    defaultAddress = "P. Paterno St, San Vicente, Biñan, Laguna",
                    registeredDate = "2026-01-05"
                ),
                UserEntity(
                    id = "owner-2",
                    name = "Miguel Santos",
                    email = "owner2@southwoodsflora.ph",
                    password = "password123",
                    role = "flower_owner",
                    status = "active",
                    phone = "0919-444-5555",
                    defaultBarangay = "San Francisco (Halang)",
                    defaultAddress = "Southwoods Blvd, San Francisco, Biñan, Laguna",
                    registeredDate = "2026-01-08"
                ),
                UserEntity(
                    id = "admin-1",
                    name = "Marc Oliver",
                    email = "admin@elaya.ph",
                    password = "password123",
                    role = "admin",
                    status = "active",
                    phone = "0920-999-0000",
                    defaultBarangay = "Poblacion",
                    defaultAddress = "Biñan City Hall Complex, Poblacion, Biñan, Laguna",
                    registeredDate = "2026-01-01"
                )
            )
            initialUsers.forEach { userDao.insertUser(it) }

            // 2. Flower Shops
            val initialShops = listOf(
                FlowerShopEntity(
                    id = "shop-1",
                    ownerId = "owner-1",
                    shopName = "Bella Flora Biñan",
                    description = "Biñan's premier artisan florist specializing in Ecuadorian roses, bridal arrangements, and custom 3D bouquets.",
                    location = "P. Paterno St., San Vicente, Biñan, Laguna",
                    barangay = "San Vicente",
                    contactInfo = "0918-777-1234",
                    rating = 4.9,
                    reviewCount = 156,
                    status = "active",
                    openingHours = "7:30 AM - 8:30 PM"
                ),
                FlowerShopEntity(
                    id = "shop-2",
                    ownerId = "owner-2",
                    shopName = "Southwoods Floral Co.",
                    description = "Modern botanical creations, sunflower hampers, and express doorstep delivery across all Biñan barangays.",
                    location = "Southwoods Avenue, San Francisco (Halang), Biñan, Laguna",
                    barangay = "San Francisco (Halang)",
                    contactInfo = "0919-444-5555",
                    rating = 4.8,
                    reviewCount = 98,
                    status = "active",
                    openingHours = "8:00 AM - 8:00 PM"
                ),
                FlowerShopEntity(
                    id = "shop-3",
                    ownerId = "owner-1",
                    shopName = "Laguna Blossom Studio",
                    description = "Freshly harvested local blooms, curated floral keepsakes, and scented pastel arrangements.",
                    location = "Old National Highway near Pavilion, Canlalay, Biñan, Laguna",
                    barangay = "Canlalay",
                    contactInfo = "0922-333-8888",
                    rating = 4.7,
                    reviewCount = 74,
                    status = "active",
                    openingHours = "9:00 AM - 7:00 PM"
                )
            )
            initialShops.forEach { shopDao.insertShop(it) }

            // 3. Products: Flowers, Bouquets, Wrappings
            val initialProducts = listOf(
                // Flowers (Shop 1 - Elena)
                ProductEntity(
                    id = "prod-f1",
                    shopId = "shop-1",
                    ownerId = "owner-1",
                    productName = "Red Ecuadorian Rose",
                    productType = "flower",
                    category = "Roses",
                    description = "Deep scarlet velvety fresh-cut Ecuadorian rose with long sturdy stem and subtle sweet fragrance.",
                    imageUrl = "https://images.unsplash.com/photo-1518895949257-7621c3c786d7?w=600",
                    price = 50.0,
                    stock = 120,
                    status = "Available",
                    availableColors = "Red,Pink,White,Peach"
                ),
                ProductEntity(
                    id = "prod-f2",
                    shopId = "shop-1",
                    ownerId = "owner-1",
                    productName = "Dutch Pastel Tulip",
                    productType = "flower",
                    category = "Tulips",
                    description = "Imported Dutch Holland tulips in pastel tones. Symbol of deep love and spring elegance.",
                    imageUrl = "https://images.unsplash.com/photo-1520763185298-1b434c919102?w=600",
                    price = 75.0,
                    stock = 80,
                    status = "Available",
                    availableColors = "Pink,Yellow,Purple,White"
                ),
                ProductEntity(
                    id = "prod-f3",
                    shopId = "shop-1",
                    ownerId = "owner-1",
                    productName = "Royal Stargazer Lily",
                    productType = "flower",
                    category = "Lilies",
                    description = "Fragrant pink star-shaped petals with deep ruby freckles and vibrant gold stamens.",
                    imageUrl = "https://images.unsplash.com/photo-1533616688419-b7a585564586?w=600",
                    price = 110.0,
                    stock = 45,
                    status = "Available",
                    availableColors = "Pink,White"
                ),

                // Flowers (Shop 2 - Miguel)
                ProductEntity(
                    id = "prod-f4",
                    shopId = "shop-2",
                    ownerId = "owner-2",
                    productName = "Golden Sunburst Sunflower",
                    productType = "flower",
                    category = "Sunflowers",
                    description = "Vibrant giant golden yellow petals with dark seed centers. Radiant, cheerful, and long-lasting.",
                    imageUrl = "https://images.unsplash.com/photo-1597848212624-a19eb35e2651?w=600",
                    price = 65.0,
                    stock = 90,
                    status = "Available",
                    availableColors = "Golden Yellow"
                ),
                ProductEntity(
                    id = "prod-f5",
                    shopId = "shop-2",
                    ownerId = "owner-2",
                    productName = "Soft Ruffled Carnation",
                    productType = "flower",
                    category = "Carnations",
                    description = "Lush fringed blooms in tender pastel tones. Symbolizes heartfelt admiration and devotion.",
                    imageUrl = "https://images.unsplash.com/photo-1582794543139-8ac9cb0f7b11?w=600",
                    price = 45.0,
                    stock = 150,
                    status = "Available",
                    availableColors = "Blush Pink,Peach,White,Crimson"
                ),
                ProductEntity(
                    id = "prod-f6",
                    shopId = "shop-2",
                    ownerId = "owner-2",
                    productName = "Purple Phalaenopsis Orchid",
                    productType = "flower",
                    category = "Orchids",
                    description = "Exotic moth orchid stem with multiple cascading blossoms in imperial purple.",
                    imageUrl = "https://images.unsplash.com/photo-1525310072745-f49212b5ac6d?w=600",
                    price = 140.0,
                    stock = 30,
                    status = "Available",
                    availableColors = "Purple,White,Magenta"
                ),

                // Bouquets (Shop 1 - Elena)
                ProductEntity(
                    id = "prod-b1",
                    shopId = "shop-1",
                    ownerId = "owner-1",
                    productName = "Romantic Red Roses",
                    productType = "bouquet",
                    category = "Romantic",
                    description = "Classic one dozen fresh Ecuadorian red roses carefully gathered with white frosted wrapping and ruby ribbon.",
                    imageUrl = "https://images.unsplash.com/photo-1561181286-d3fee7d55364?w=600",
                    price = 850.0,
                    stock = 15,
                    status = "Available",
                    flowersIncluded = "12 Ecuadorian Red Roses, Baby's Breath filler",
                    numberOfFlowers = 12,
                    wrapping = "White Matte Wrapper",
                    ribbon = "Red Satin Ribbon"
                ),
                ProductEntity(
                    id = "prod-b2",
                    shopId = "shop-1",
                    ownerId = "owner-1",
                    productName = "Blushing Sweetheart Arrangement",
                    productType = "bouquet",
                    category = "Romantic",
                    description = "Delicate blush pink roses and cream carnations embraced in soft pastel pink wrapping.",
                    imageUrl = "https://images.unsplash.com/photo-1587556930799-8dca6a63681f?w=600",
                    price = 920.0,
                    stock = 12,
                    status = "Available",
                    flowersIncluded = "10 Blush Roses, 6 Carnations, Eucalyptus",
                    numberOfFlowers = 16,
                    wrapping = "Pastel Pink Wrapper",
                    ribbon = "Rose Gold Ribbon"
                ),

                // Bouquets (Shop 2 - Miguel)
                ProductEntity(
                    id = "prod-b3",
                    shopId = "shop-2",
                    ownerId = "owner-2",
                    productName = "Radiant Sunflower Euphoria",
                    productType = "bouquet",
                    category = "Celebration",
                    description = "Six golden sunflowers paired with white statice and eco-friendly rustic Kraft wrapping paper.",
                    imageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=600",
                    price = 890.0,
                    stock = 18,
                    status = "Available",
                    flowersIncluded = "6 Sunflowers, Statice fillers, Ruscus leaves",
                    numberOfFlowers = 6,
                    wrapping = "Kraft Paper",
                    ribbon = "Natural Burlap & Gold Ribbon"
                ),
                ProductEntity(
                    id = "prod-b4",
                    shopId = "shop-2",
                    ownerId = "owner-2",
                    productName = "Enchanted Spring Garden",
                    productType = "bouquet",
                    category = "Special",
                    description = "Vibrant multi-floral bouquet with Dutch tulips, golden carnations, and lilac orchids.",
                    imageUrl = "https://images.unsplash.com/photo-1567696911980-2eed69a46042?w=600",
                    price = 1350.0,
                    stock = 8,
                    status = "Available",
                    flowersIncluded = "8 Tulips, 6 Carnations, 2 Orchids",
                    numberOfFlowers = 16,
                    wrapping = "Cream French Wrapper",
                    ribbon = "Champagne Gold Ribbon"
                ),

                // Wrappings (Selectable by customers in Bouquet Studio!)
                ProductEntity(
                    id = "prod-w1",
                    shopId = "shop-1",
                    ownerId = "owner-1",
                    productName = "Kraft Paper",
                    productType = "wrapping",
                    category = "Wrapping",
                    description = "Eco-friendly premium rustic brown Kraft wrapping paper. Clean, organic, and earth-toned.",
                    imageUrl = "https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=600",
                    price = 30.0,
                    stock = 250,
                    status = "Available",
                    wrapColorStyle = "Natural Rustic Brown"
                ),
                ProductEntity(
                    id = "prod-w2",
                    shopId = "shop-1",
                    ownerId = "owner-1",
                    productName = "Pastel Pink Wrapper",
                    productType = "wrapping",
                    category = "Wrapping",
                    description = "Waterproof matte pastel blush pink paper with scalloped luxury edges.",
                    imageUrl = "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=600",
                    price = 45.0,
                    stock = 200,
                    status = "Available",
                    wrapColorStyle = "Pastel Blush Pink"
                ),
                ProductEntity(
                    id = "prod-w3",
                    shopId = "shop-1",
                    ownerId = "owner-1",
                    productName = "White Matte Wrapper",
                    productType = "wrapping",
                    category = "Wrapping",
                    description = "Ultra-clean modern pure white matte wrapping paper. Perfect for minimalist and bridal aesthetics.",
                    imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600",
                    price = 40.0,
                    stock = 200,
                    status = "Available",
                    wrapColorStyle = "Pure Pearl White"
                ),
                ProductEntity(
                    id = "prod-w4",
                    shopId = "shop-2",
                    ownerId = "owner-2",
                    productName = "Cream French Wrapper",
                    productType = "wrapping",
                    category = "Wrapping",
                    description = "Soft ivory Parisian cream paper with subtle metallic gold line border.",
                    imageUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=600",
                    price = 50.0,
                    stock = 180,
                    status = "Available",
                    wrapColorStyle = "Cream & Gold Accent"
                ),
                ProductEntity(
                    id = "prod-w5",
                    shopId = "shop-2",
                    ownerId = "owner-2",
                    productName = "Floral Printed Wrap",
                    productType = "wrapping",
                    category = "Wrapping",
                    description = "Artistic vintage botanical illustration pattern on waterproof semi-gloss paper.",
                    imageUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600",
                    price = 55.0,
                    stock = 150,
                    status = "Available",
                    wrapColorStyle = "Vintage Botanical Print"
                ),
                ProductEntity(
                    id = "prod-w6",
                    shopId = "shop-2",
                    ownerId = "owner-2",
                    productName = "Transparent Crystal Wrap",
                    productType = "wrapping",
                    category = "Wrapping",
                    description = "Clear iridescent cellophane film that captures prism reflections in the light.",
                    imageUrl = "https://images.unsplash.com/photo-1518895949257-7621c3c786d7?w=600",
                    price = 40.0,
                    stock = 160,
                    status = "Available",
                    wrapColorStyle = "Prismatic Translucent"
                )
            )
            initialProducts.forEach { productDao.insertProduct(it) }

            // 4. Initial Orders (for tracking demonstration & owner order fulfillment)
            val initialOrders = listOf(
                OrderEntity(
                    id = "ELA-2026-101",
                    customerId = "cust-1",
                    customerName = "Sofia Andres",
                    customerPhone = "0917-234-5678",
                    shopId = "shop-1",
                    shopName = "Bella Flora Biñan",
                    productsSummary = "1x Romantic Red Roses (12 Stems)",
                    quantity = 1,
                    totalPrice = 899.0,
                    deliveryAddress = "Blk 14 Lot 8, Villa Biñan Subd., San Vicente, Biñan, Laguna",
                    barangay = "San Vicente",
                    orderDate = "2026-09-23 09:15 AM",
                    paymentStatus = "Paid (GCash)",
                    orderStatus = "Out for Delivery",
                    riderName = "Dennis Dela Cruz",
                    riderPhone = "0920-555-7890",
                    riderVehicle = "Honda Click 125i (Plate: 4024-DF)",
                    etaMinutes = 14,
                    dispatchNotes = "Gate 2 intercom code #1408. Fragile floral box."
                ),
                OrderEntity(
                    id = "ELA-2026-102",
                    customerId = "cust-1",
                    customerName = "Sofia Andres",
                    customerPhone = "0917-234-5678",
                    shopId = "shop-2",
                    shopName = "Southwoods Floral Co.",
                    productsSummary = "1x Radiant Sunflower Euphoria (Kraft Wrap)",
                    quantity = 1,
                    totalPrice = 939.0,
                    deliveryAddress = "Unit 402, Southwoods Ecocentrum, San Francisco, Biñan, Laguna",
                    barangay = "San Francisco (Halang)",
                    orderDate = "2026-09-23 08:30 AM",
                    paymentStatus = "Paid (Maya)",
                    orderStatus = "Preparing",
                    riderName = "Carlos Mendoza",
                    riderPhone = "0917-888-2345",
                    riderVehicle = "Yamaha NMAX (Plate: 4024-CM)",
                    etaMinutes = 28,
                    dispatchNotes = "Include birthday card 'Happy 25th Birthday Chloe!'"
                ),
                OrderEntity(
                    id = "ELA-2026-103",
                    customerId = "cust-2",
                    customerName = "Karlo Bautista",
                    customerPhone = "0918-999-3322",
                    shopId = "shop-1",
                    shopName = "Bella Flora Biñan",
                    productsSummary = "1x Custom 3D Bouquet (24 Pink Tulips & Cream Wrap)",
                    quantity = 1,
                    totalPrice = 1450.0,
                    deliveryAddress = "Canlalay Highway near Pavilion Mall, Canlalay, Biñan, Laguna",
                    barangay = "Canlalay",
                    orderDate = "2026-09-22 04:45 PM",
                    paymentStatus = "Paid (GCash)",
                    orderStatus = "Completed",
                    riderName = "Dennis Dela Cruz",
                    riderPhone = "0920-555-7890",
                    riderVehicle = "Honda Click 125i",
                    etaMinutes = 0,
                    dispatchNotes = "Delivered successfully at security reception."
                )
            )
            initialOrders.forEach { orderDao.insertOrder(it) }
        }
    }
}
