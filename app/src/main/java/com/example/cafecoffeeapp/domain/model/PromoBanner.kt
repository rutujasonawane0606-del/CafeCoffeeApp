package com.example.cafecoffeeapp.domain.model

data class PromoBanner(
    val id: String = "active_banner",
    val title: String = "Special Offer",
    val description: String = "Buy one get one FREE on all handcrafted coffees",
    val imageUrl: String = "",
    val promoCode: String = "COFFEE50",
    val discountPercent: Int = 20,
    val isActive: Boolean = true
)
