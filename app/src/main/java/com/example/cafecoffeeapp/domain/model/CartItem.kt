package com.example.cafecoffeeapp.domain.model

data class CartItem(
    val id: Long = 0,
    val productId: Int,
    val name: String,
    val category: String,
    val price: Double,
    val imageRes: Int,
    val size: String = "M",
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = price * quantity
}
