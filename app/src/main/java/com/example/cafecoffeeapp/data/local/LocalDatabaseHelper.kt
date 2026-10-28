package com.example.cafecoffeeapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.cafecoffeeapp.data.local.dao.CartDao
import com.example.cafecoffeeapp.data.local.dao.FavoriteDao
import com.example.cafecoffeeapp.data.local.dao.OrderDao
import com.example.cafecoffeeapp.data.local.entity.CartItemEntity
import com.example.cafecoffeeapp.data.local.entity.FavoriteEntity
import com.example.cafecoffeeapp.data.local.entity.OrderEntity
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val cartTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val favoriteTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val orderTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        cartTrigger.tryEmit(Unit)
        favoriteTrigger.tryEmit(Unit)
        orderTrigger.tryEmit(Unit)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS cart_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                productId INTEGER NOT NULL,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                price REAL NOT NULL,
                imageRes INTEGER NOT NULL,
                size TEXT NOT NULL,
                quantity INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS favorites (
                productId INTEGER PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                description TEXT NOT NULL,
                detailedDescription TEXT NOT NULL,
                price REAL NOT NULL,
                imageRes INTEGER NOT NULL,
                rating REAL NOT NULL,
                reviewsCount INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS orders (
                orderId TEXT PRIMARY KEY NOT NULL,
                timestamp INTEGER NOT NULL,
                itemsJson TEXT NOT NULL,
                subtotal REAL NOT NULL,
                deliveryFee REAL NOT NULL,
                taxes REAL NOT NULL,
                totalAmount REAL NOT NULL,
                deliveryAddress TEXT NOT NULL,
                paymentMethod TEXT NOT NULL,
                orderStatus TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS cart_items")
        db.execSQL("DROP TABLE IF EXISTS favorites")
        db.execSQL("DROP TABLE IF EXISTS orders")
        onCreate(db)
    }

    val cartDao: CartDao = object : CartDao {
        override fun getAllCartItems(): Flow<List<CartItemEntity>> {
            return kotlinx.coroutines.flow.flow {
                cartTrigger.collect {
                    val list = mutableListOf<CartItemEntity>()
                    val cursor = readableDatabase.rawQuery(
                        "SELECT * FROM cart_items ORDER BY id DESC",
                        null
                    )
                    cursor.use {
                        while (cursor.moveToNext()) {
                            list.add(
                                CartItemEntity(
                                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                                    productId = cursor.getInt(cursor.getColumnIndexOrThrow("productId")),
                                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                                    price = cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                                    imageRes = cursor.getInt(cursor.getColumnIndexOrThrow("imageRes")),
                                    size = cursor.getString(cursor.getColumnIndexOrThrow("size")),
                                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                                )
                            )
                        }
                    }
                    emit(list)
                }
            }
        }

        override suspend fun getCartItemByProductAndSize(
            productId: Int,
            size: String
        ): CartItemEntity? {
            val cursor = readableDatabase.rawQuery(
                "SELECT * FROM cart_items WHERE productId = ? AND size = ? LIMIT 1",
                arrayOf(productId.toString(), size)
            )
            cursor.use {
                if (cursor.moveToFirst()) {
                    return CartItemEntity(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        productId = cursor.getInt(cursor.getColumnIndexOrThrow("productId")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        price = cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        imageRes = cursor.getInt(cursor.getColumnIndexOrThrow("imageRes")),
                        size = cursor.getString(cursor.getColumnIndexOrThrow("size")),
                        quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                    )
                }
            }
            return null
        }

        override suspend fun insert(cartItem: CartItemEntity): Long {
            val cv = ContentValues().apply {
                put("productId", cartItem.productId)
                put("name", cartItem.name)
                put("category", cartItem.category)
                put("price", cartItem.price)
                put("imageRes", cartItem.imageRes)
                put("size", cartItem.size)
                put("quantity", cartItem.quantity)
            }
            val id = writableDatabase.insertWithOnConflict(
                "cart_items",
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
            )
            cartTrigger.tryEmit(Unit)
            return id
        }

        override suspend fun update(cartItem: CartItemEntity) {
            val cv = ContentValues().apply {
                put("productId", cartItem.productId)
                put("name", cartItem.name)
                put("category", cartItem.category)
                put("price", cartItem.price)
                put("imageRes", cartItem.imageRes)
                put("size", cartItem.size)
                put("quantity", cartItem.quantity)
            }
            writableDatabase.update("cart_items", cv, "id = ?", arrayOf(cartItem.id.toString()))
            cartTrigger.tryEmit(Unit)
        }

        override suspend fun delete(cartItem: CartItemEntity) {
            writableDatabase.delete("cart_items", "id = ?", arrayOf(cartItem.id.toString()))
            cartTrigger.tryEmit(Unit)
        }

        override suspend fun clearCart() {
            writableDatabase.delete("cart_items", null, null)
            cartTrigger.tryEmit(Unit)
        }
    }

    val favoriteDao: FavoriteDao = object : FavoriteDao {
        override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
            return kotlinx.coroutines.flow.flow {
                favoriteTrigger.collect {
                    val list = mutableListOf<FavoriteEntity>()
                    val cursor = readableDatabase.rawQuery(
                        "SELECT * FROM favorites ORDER BY name ASC",
                        null
                    )
                    cursor.use {
                        while (cursor.moveToNext()) {
                            list.add(
                                FavoriteEntity(
                                    productId = cursor.getInt(cursor.getColumnIndexOrThrow("productId")),
                                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                                    detailedDescription = cursor.getString(cursor.getColumnIndexOrThrow("detailedDescription")),
                                    price = cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                                    imageRes = cursor.getInt(cursor.getColumnIndexOrThrow("imageRes")),
                                    rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                                    reviewsCount = cursor.getInt(cursor.getColumnIndexOrThrow("reviewsCount"))
                                )
                            )
                        }
                    }
                    emit(list)
                }
            }
        }

        override fun isFavorite(productId: Int): Flow<Boolean> {
            return kotlinx.coroutines.flow.flow {
                favoriteTrigger.collect {
                    val cursor = readableDatabase.rawQuery(
                        "SELECT 1 FROM favorites WHERE productId = ? LIMIT 1",
                        arrayOf(productId.toString())
                    )
                    val exists = cursor.use { it.moveToFirst() }
                    emit(exists)
                }
            }
        }

        override suspend fun insert(favorite: FavoriteEntity) {
            val cv = ContentValues().apply {
                put("productId", favorite.productId)
                put("name", favorite.name)
                put("category", favorite.category)
                put("description", favorite.description)
                put("detailedDescription", favorite.detailedDescription)
                put("price", favorite.price)
                put("imageRes", favorite.imageRes)
                put("rating", favorite.rating)
                put("reviewsCount", favorite.reviewsCount)
            }
            writableDatabase.insertWithOnConflict("favorites", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            favoriteTrigger.tryEmit(Unit)
        }

        override suspend fun delete(favorite: FavoriteEntity) {
            deleteByProductId(favorite.productId)
        }

        override suspend fun deleteByProductId(productId: Int) {
            writableDatabase.delete("favorites", "productId = ?", arrayOf(productId.toString()))
            favoriteTrigger.tryEmit(Unit)
        }
    }

    val orderDao: OrderDao = object : OrderDao {
        override fun getAllOrders(): Flow<List<OrderEntity>> {
            return kotlinx.coroutines.flow.flow {
                orderTrigger.collect {
                    val list = mutableListOf<OrderEntity>()
                    val cursor = readableDatabase.rawQuery(
                        "SELECT * FROM orders ORDER BY timestamp DESC",
                        null
                    )
                    cursor.use {
                        while (cursor.moveToNext()) {
                            list.add(
                                OrderEntity(
                                    orderId = cursor.getString(cursor.getColumnIndexOrThrow("orderId")),
                                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                                    itemsJson = cursor.getString(cursor.getColumnIndexOrThrow("itemsJson")),
                                    subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")),
                                    deliveryFee = cursor.getDouble(cursor.getColumnIndexOrThrow("deliveryFee")),
                                    taxes = cursor.getDouble(cursor.getColumnIndexOrThrow("taxes")),
                                    totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount")),
                                    deliveryAddress = cursor.getString(cursor.getColumnIndexOrThrow("deliveryAddress")),
                                    paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow("paymentMethod")),
                                    orderStatus = cursor.getString(cursor.getColumnIndexOrThrow("orderStatus"))
                                )
                            )
                        }
                    }
                    emit(list)
                }
            }
        }

        override fun getOrderById(orderId: String): Flow<OrderEntity?> {
            return kotlinx.coroutines.flow.flow {
                orderTrigger.collect {
                    val cursor = readableDatabase.rawQuery(
                        "SELECT * FROM orders WHERE orderId = ? LIMIT 1",
                        arrayOf(orderId)
                    )
                    val item = cursor.use {
                        if (it.moveToFirst()) {
                            OrderEntity(
                                orderId = it.getString(it.getColumnIndexOrThrow("orderId")),
                                timestamp = it.getLong(it.getColumnIndexOrThrow("timestamp")),
                                itemsJson = it.getString(it.getColumnIndexOrThrow("itemsJson")),
                                subtotal = it.getDouble(it.getColumnIndexOrThrow("subtotal")),
                                deliveryFee = it.getDouble(it.getColumnIndexOrThrow("deliveryFee")),
                                taxes = it.getDouble(it.getColumnIndexOrThrow("taxes")),
                                totalAmount = it.getDouble(it.getColumnIndexOrThrow("totalAmount")),
                                deliveryAddress = it.getString(it.getColumnIndexOrThrow("deliveryAddress")),
                                paymentMethod = it.getString(it.getColumnIndexOrThrow("paymentMethod")),
                                orderStatus = it.getString(it.getColumnIndexOrThrow("orderStatus"))
                            )
                        } else null
                    }
                    emit(item)
                }
            }
        }

        override suspend fun insertOrder(order: OrderEntity) {
            val cv = ContentValues().apply {
                put("orderId", order.orderId)
                put("timestamp", order.timestamp)
                put("itemsJson", order.itemsJson)
                put("subtotal", order.subtotal)
                put("deliveryFee", order.deliveryFee)
                put("taxes", order.taxes)
                put("totalAmount", order.totalAmount)
                put("deliveryAddress", order.deliveryAddress)
                put("paymentMethod", order.paymentMethod)
                put("orderStatus", order.orderStatus)
            }
            writableDatabase.insertWithOnConflict("orders", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            orderTrigger.tryEmit(Unit)
        }

        override suspend fun updateOrder(order: OrderEntity) {
            insertOrder(order)
        }
    }

    companion object {
        private const val DATABASE_NAME = "cafe_coffee_app.db"
        private const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: LocalDatabaseHelper? = null

        fun getInstance(context: Context): LocalDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = LocalDatabaseHelper(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
