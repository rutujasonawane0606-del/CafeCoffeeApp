package com.example.cafecoffeeapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafecoffeeapp.data.repository.CoffeeRepository
import com.example.cafecoffeeapp.domain.model.CartItem
import com.example.cafecoffeeapp.domain.model.Order
import com.example.cafecoffeeapp.domain.model.Product
import com.example.cafecoffeeapp.domain.model.PromoBanner
import com.example.cafecoffeeapp.presentation.navigation.AppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoffeeViewModel(application: Application) : AndroidViewModel(application) {

    val repository = CoffeeRepository(application)

    // Navigation state - starts on Splash screen
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Splash)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Search and Category state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All Coffees")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories = repository.categories

    // Currently viewed product in Details screen
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Selected cup size in Details screen: "S", "M", "L"
    private val _selectedSize = MutableStateFlow("M")
    val selectedSize: StateFlow<String> = _selectedSize.asStateFlow()

    // Last placed order for the checkout confirmation modal
    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    // Live Promo Banner from Firebase
    val promoBanner: StateFlow<PromoBanner> = repository.getPromoBannerStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, PromoBanner())

    // Snackbar / Toast message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // All products stream from Firebase & Room
    val allProducts: StateFlow<List<Product>> = repository.getProductsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Favorites stream from Room
    val favorites: StateFlow<List<Product>> = repository.getFavoriteProductsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Cart items stream from Room
    val cartItems: StateFlow<List<CartItem>> = repository.getCartItemsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Orders stream from Room & Firebase
    val orders: StateFlow<List<Order>> = repository.getAllOrdersStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtered products matching BOTH search query and selected category
    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        searchQuery,
        selectedCategory
    ) { products, query, category ->
        products.filter { product ->
            val matchesCategory = (category == "All Coffees") ||
                    product.category.equals(category, ignoreCase = true)

            val matchesSearch = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // User actions
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun onProductSelected(product: Product) {
        _selectedProduct.value = product
        _selectedSize.value = "M"
        navigateTo(AppScreen.Details)
    }

    fun onSizeSelected(size: String) {
        _selectedSize.value = size
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            repository.toggleFavorite(product)
            val updated = product.copy(isFavorite = !product.isFavorite)
            if (_selectedProduct.value?.id == product.id) {
                _selectedProduct.value = updated
            }
            _userMessage.value = if (!product.isFavorite) "Added to Favorites" else "Removed from Favorites"
        }
    }

    fun addToCart(product: Product, size: String = _selectedSize.value) {
        viewModelScope.launch {
            repository.addToCart(product, size)
            _userMessage.value = "${product.name} (Size: $size) added to cart!"
        }
    }

    fun updateCartQuantity(cartItem: CartItem, delta: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItem, delta)
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            repository.removeFromCart(cartItem)
            _userMessage.value = "${cartItem.name} removed from cart"
        }
    }

    fun placeOrder(deliveryAddress: String, paymentMethod: String) {
        viewModelScope.launch {
            val currentItems = cartItems.value
            if (currentItems.isNotEmpty()) {
                val order = repository.placeOrder(
                    items = currentItems,
                    deliveryAddress = deliveryAddress,
                    paymentMethod = paymentMethod
                )
                _lastPlacedOrder.value = order
                _userMessage.value = "Order #${order.orderId} placed successfully!"
            }
        }
    }

    fun clearLastPlacedOrder() {
        _lastPlacedOrder.value = null
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun seedFirebaseDatabase() {
        viewModelScope.launch {
            repository.firebaseService.seedProductsToFirestore(
                onSuccess = {
                    _userMessage.value = "Firebase Cloud Firestore seeded successfully!"
                },
                onError = { e ->
                    _userMessage.value = "Firebase Seed Notice: ${e.message}"
                }
            )
        }
    }
}
