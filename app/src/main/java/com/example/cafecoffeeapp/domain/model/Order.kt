package com.example.cafecoffeeapp.domain.model

data class Order(
    val orderId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double = 30.0,
    val taxes: Double = 0.0,
    val totalAmount: Double,
    val deliveryAddress: String,
    val paymentMethod: String,
    val orderStatus: String = "Preparing"
)
