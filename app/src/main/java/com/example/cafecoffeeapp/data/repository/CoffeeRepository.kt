package com.example.cafecoffeeapp.data.repository

import android.content.Context
import com.example.cafecoffeeapp.data.datasource.CoffeeDataSource
import com.example.cafecoffeeapp.data.local.CafeCoffeeDatabaseProvider
import com.example.cafecoffeeapp.data.local.entity.CartItemEntity
import com.example.cafecoffeeapp.data.local.entity.FavoriteEntity
import com.example.cafecoffeeapp.data.local.entity.OrderEntity
import com.example.cafecoffeeapp.data.remote.FirebaseCoffeeService
import com.example.cafecoffeeapp.domain.model.CartItem
import com.example.cafecoffeeapp.domain.model.Order
import com.example.cafecoffeeapp.domain.model.Product
import com.example.cafecoffeeapp.domain.model.PromoBanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CoffeeRepository(context: Context) {

    private val dbProvider = CafeCoffeeDatabaseProvider.getInstance(context)
    private val cartDao = dbProvider.cartDao
    private val favoriteDao = dbProvider.favoriteDao
    private val orderDao = dbProvider.orderDao

    val firebaseService = FirebaseCoffeeService(context)

    /**
     * Categories available in the coffee shop
     */
    val categories: List<String> = CoffeeDataSource.defaultCategories

    /**
     * Live Promo Banner stream from Firebase Firestore
     */
    fun getPromoBannerStream(): Flow<PromoBanner> {
        return firebaseService.getPromoBannerFlow()
    }

    /**
     * Get all products combined with favorite status
     */
    fun getProductsStream(): Flow<List<Product>> {
        val baseProductsFlow = firebaseService.getProductsFlow()
        val favoritesFlow = favoriteDao.getAllFavorites()

        return combine(baseProductsFlow, favoritesFlow) { products, favorites ->
            val favIds = favorites.map { it.productId }.toSet()
            products.map { product ->
                product.copy(isFavorite = favIds.contains(product.id))
            }
        }
    }

    /**
     * Get all favorited products
     */
    fun getFavoriteProductsStream(): Flow<List<Product>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { it.toProduct() }
        }
    }

    /**
     * Check if a product is favorited
     */
    fun isProductFavorite(productId: Int): Flow<Boolean> {
        return favoriteDao.isFavorite(productId)
    }

    /**
     * Toggle favorite status
     */
    suspend fun toggleFavorite(product: Product) {
        val currentFavs = favoriteDao.getAllFavorites().firstOrNull() ?: emptyList()
        val exists = currentFavs.any { it.productId == product.id }
        if (exists) {
            favoriteDao.deleteByProductId(product.id)
        } else {
            favoriteDao.insert(FavoriteEntity.fromProduct(product))
        }
    }

    /**
     * Get all cart items
     */
    fun getCartItemsStream(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Add product to cart with size
     */
    suspend fun addToCart(product: Product, size: String = "M") {
        // Adjust price slightly based on size
        val adjustedPrice = when (size) {
            "S" -> (product.price - 40.0).coerceAtLeast(100.0)
            "L" -> product.price + 50.0
            else -> product.price
        }

        val existing = cartDao.getCartItemByProductAndSize(product.id, size)
        if (existing != null) {
            val updated = existing.copy(quantity = existing.quantity + 1)
            cartDao.update(updated)
        } else {
            val newEntity = CartItemEntity(
                productId = product.id,
                name = product.name,
                category = product.category,
                price = adjustedPrice,
                imageRes = product.imageRes,
                size = size,
                quantity = 1
            )
            cartDao.insert(newEntity)
        }
    }

    /**
     * Update quantity of a cart item
     */
    suspend fun updateCartQuantity(cartItem: CartItem, delta: Int) {
        val newQty = cartItem.quantity + delta
        if (newQty <= 0) {
            cartDao.delete(CartItemEntity.fromDomain(cartItem))
        } else {
            cartDao.update(CartItemEntity.fromDomain(cartItem.copy(quantity = newQty)))
        }
    }

    /**
     * Remove item from cart
     */
    suspend fun removeFromCart(cartItem: CartItem) {
        cartDao.delete(CartItemEntity.fromDomain(cartItem))
    }

    /**
     * Clear all items in cart
     */
    suspend fun clearCart() {
        cartDao.clearCart()
    }

    /**
     * Place order: saves to Room database and pushes to Firebase Firestore
     */
    suspend fun placeOrder(
        items: List<CartItem>,
        deliveryAddress: String,
        paymentMethod: String
    ): Order {
        val subtotal = items.sumOf { it.totalPrice }
        val deliveryFee = if (subtotal > 500.0 || items.isEmpty()) 0.0 else 30.0
        val taxes = Math.round(subtotal * 0.05 * 100.0) / 100.0 // 5% GST
        val totalAmount = subtotal + deliveryFee + taxes

        val orderNumber = (10000..99999).random()
        val orderId = "ORD-$orderNumber"

        val order = Order(
            orderId = orderId,
            timestamp = System.currentTimeMillis(),
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            taxes = taxes,
            totalAmount = totalAmount,
            deliveryAddress = deliveryAddress,
            paymentMethod = paymentMethod,
            orderStatus = "Preparing"
        )

        // 1. Save in local Room Database
        orderDao.insertOrder(OrderEntity.fromOrder(order))

        // 2. Clear current cart
        cartDao.clearCart()

        // 3. Upload to Firebase Firestore (non-blocking / offline-safe)
        firebaseService.saveOrderToFirestore(order)

        return order
    }

    /**
     * Get all past orders from Room
     */
    fun getAllOrdersStream(): Flow<List<Order>> {
        return orderDao.getAllOrders().map { entities ->
            entities.map { it.toOrder() }
        }
    }
}
