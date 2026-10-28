package com.example.cafecoffeeapp.data.remote

import android.content.Context
import android.util.Log
import com.example.cafecoffeeapp.data.datasource.CoffeeDataSource
import com.example.cafecoffeeapp.domain.model.Order
import com.example.cafecoffeeapp.domain.model.Product
import com.example.cafecoffeeapp.domain.model.PromoBanner
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject

class FirebaseCoffeeService(private val context: Context) {

    private val tag = "FirebaseCoffeeService"

    init {
        ensureFirebaseInitialized()
    }

    private fun ensureFirebaseInitialized() {
        if (FirebaseApp.getApps(context).isEmpty()) {
            // Attempt 1: Standard initialization via Google Services resources
            try {
                FirebaseApp.initializeApp(context)
                Log.d(tag, "Firebase initialized via default provider")
            } catch (e: Exception) {
                Log.w(tag, "Default Firebase initialization did not find resources, trying fallback: ${e.message}")
            }

            // Attempt 2: Programmatic fallback with project credentials
            if (FirebaseApp.getApps(context).isEmpty()) {
                try {
                    val options = FirebaseOptions.Builder()
                        .setApplicationId("1:772224745012:android:dfc3a3bd3a15b0438e9461")
                        .setApiKey("AIzaSyCu054cSzYwzFJUyg7XKDC76_nhavDnwHM")
                        .setProjectId("cafecoffeeapp")
                        .setStorageBucket("cafecoffeeapp.firebasestorage.app")
                        .build()
                    FirebaseApp.initializeApp(context, options)
                    Log.d(tag, "Firebase initialized via programmatic options!")
                } catch (e: Exception) {
                    Log.e(tag, "Programmatic Firebase initialization error: ${e.message}")
                }
            }
        }
    }

    val isFirebaseInitialized: Boolean
        get() {
            ensureFirebaseInitialized()
            return try {
                FirebaseApp.getApps(context).isNotEmpty()
            } catch (e: Throwable) {
                Log.w(tag, "Firebase initialization check failed: ${e.message}")
                false
            }
        }

    private val firestore: FirebaseFirestore?
        get() = if (isFirebaseInitialized) {
            try {
                FirebaseFirestore.getInstance()
            } catch (e: Throwable) {
                Log.w(tag, "Could not get Firestore instance: ${e.message}")
                null
            }
        } else {
            null
        }

    /**
     * Listen to real-time products collection from Firebase Firestore
     */
    fun getProductsFlow(): Flow<List<Product>> = callbackFlow {
        ensureFirebaseInitialized()
        val db = firestore
        if (db == null) {
            // Firebase not active yet, return local default products
            trySend(CoffeeDataSource.defaultProducts)
            close()
            return@callbackFlow
        }

        val listener = db.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(tag, "Error listening for products: ${error.message}")
                    trySend(CoffeeDataSource.defaultProducts)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val products = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = (doc.getLong("id") ?: doc.id.hashCode().toLong()).toInt()
                            val name = doc.getString("name") ?: ""
                            val category = doc.getString("category") ?: "All Coffees"
                            val description = doc.getString("description") ?: ""
                            val detailedDescription = doc.getString("detailedDescription") ?: ""
                            val price = doc.getDouble("price") ?: 0.0
                            val imageUrl = doc.getString("imageUrl") ?: ""
                            val rating = doc.getDouble("rating") ?: 4.8
                            val reviewsCount = (doc.getLong("reviewsCount") ?: 100L).toInt()

                            // Fallback to local drawable if imageRes matching
                            val fallbackRes = CoffeeDataSource.defaultProducts.firstOrNull { it.id == id }?.imageRes
                                ?: com.example.cafecoffeeapp.R.drawable.coffee_1

                            Product(
                                id = id,
                                name = name,
                                category = category,
                                description = description,
                                detailedDescription = detailedDescription,
                                price = price,
                                imageRes = fallbackRes,
                                imageUrl = imageUrl,
                                rating = rating,
                                reviewsCount = reviewsCount
                            )
                        } catch (e: Exception) {
                            Log.e(tag, "Error parsing product document: ${e.message}")
                            null
                        }
                    }
                    trySend(products)
                } else {
                    // Collection is empty, emit default products
                    trySend(CoffeeDataSource.defaultProducts)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Listen to real-time banner document from Firebase Firestore
     */
    fun getPromoBannerFlow(): Flow<PromoBanner> = callbackFlow {
        ensureFirebaseInitialized()
        val db = firestore
        if (db == null) {
            trySend(PromoBanner())
            close()
            return@callbackFlow
        }

        val listener = db.collection("banners")
            .document("active_banner")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(tag, "Error listening for banner: ${error.message}")
                    trySend(PromoBanner())
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val banner = PromoBanner(
                        id = snapshot.getString("id") ?: "active_banner",
                        title = snapshot.getString("title") ?: "Special Offer",
                        description = snapshot.getString("description") ?: "Buy one get one FREE on all handcrafted coffees",
                        imageUrl = snapshot.getString("imageUrl") ?: "",
                        promoCode = snapshot.getString("promoCode") ?: "COFFEE50",
                        discountPercent = (snapshot.getLong("discountPercent") ?: 20L).toInt(),
                        isActive = snapshot.getBoolean("isActive") ?: true
                    )
                    trySend(banner)
                } else {
                    trySend(PromoBanner())
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Upload an order to Firebase Firestore under collection 'orders'
     */
    fun saveOrderToFirestore(
        order: Order,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        ensureFirebaseInitialized()
        val db = firestore
        if (db == null) {
            Log.d(tag, "Firebase not active. Order #${order.orderId} kept in local Room database.")
            onSuccess()
            return
        }

        val orderMap = hashMapOf(
            "orderId" to order.orderId,
            "timestamp" to order.timestamp,
            "subtotal" to order.subtotal,
            "deliveryFee" to order.deliveryFee,
            "taxes" to order.taxes,
            "totalAmount" to order.totalAmount,
            "deliveryAddress" to order.deliveryAddress,
            "paymentMethod" to order.paymentMethod,
            "orderStatus" to order.orderStatus,
            "items" to order.items.map { item ->
                hashMapOf(
                    "id" to item.id,
                    "productId" to item.productId,
                    "name" to item.name,
                    "category" to item.category,
                    "price" to item.price,
                    "size" to item.size,
                    "quantity" to item.quantity
                )
            }
        )

        db.collection("orders")
            .document(order.orderId)
            .set(orderMap, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(tag, "Order #${order.orderId} synced with Firebase Firestore!")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Failed to upload order to Firestore: ${e.message}")
                onError(e)
            }
    }

    /**
     * Seed initial coffee menu data to Firebase Firestore
     */
    fun seedProductsToFirestore(
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        ensureFirebaseInitialized()
        val db = firestore
        if (db == null) {
            onError(IllegalStateException("Firebase is still not ready. Please re-run the app in Android Studio."))
            return
        }

        val batch = db.batch()
        val productsCol = db.collection("products")
        val ordersCol = db.collection("orders")

        CoffeeDataSource.defaultProducts.forEach { product ->
            val docRef = productsCol.document("prod_${product.id}")
            val map = hashMapOf(
                "id" to product.id,
                "name" to product.name,
                "category" to product.category,
                "description" to product.description,
                "detailedDescription" to product.detailedDescription,
                "price" to product.price,
                "imageUrl" to product.imageUrl,
                "rating" to product.rating,
                "reviewsCount" to product.reviewsCount
            )
            batch.set(docRef, map, SetOptions.merge())
        }

        // Seed an initial demo order so the 'orders' collection is visible immediately
        val demoOrderRef = ordersCol.document("ORD-10001")
        val demoOrderMap = hashMapOf(
            "orderId" to "ORD-10001",
            "timestamp" to System.currentTimeMillis(),
            "subtotal" to 450.0,
            "deliveryFee" to 0.0,
            "taxes" to 22.5,
            "totalAmount" to 472.5,
            "deliveryAddress" to "Flat 402, Green Acre Heights, Bibwewadi, Pune - 411037",
            "paymentMethod" to "UPI / Mobile Banking",
            "orderStatus" to "Delivered",
            "items" to listOf(
                hashMapOf(
                    "id" to 1L,
                    "productId" to 2,
                    "name" to "Caramel Latte",
                    "category" to "Latte",
                    "price" to 450.0,
                    "size" to "M",
                    "quantity" to 1
                )
            )
        )
        batch.set(demoOrderRef, demoOrderMap, SetOptions.merge())

        // Seed default promo banner document in 'banners' collection
        val bannersCol = db.collection("banners")
        val bannerRef = bannersCol.document("active_banner")
        val bannerMap = hashMapOf(
            "id" to "active_banner",
            "title" to "Special Offer",
            "description" to "Buy 1 Get 1 FREE on all handcrafted coffees",
            "imageUrl" to "", // Paste any offer image URL here in Firebase Console!
            "promoCode" to "COFFEE50",
            "discountPercent" to 20,
            "isActive" to true
        )
        batch.set(bannerRef, bannerMap, SetOptions.merge())

        batch.commit()
            .addOnSuccessListener {
                Log.d(tag, "Successfully seeded products, orders, and banner to Firestore!")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Failed to seed products: ${e.message}")
                onError(e)
            }
    }
}
