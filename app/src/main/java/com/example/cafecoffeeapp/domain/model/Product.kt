package com.example.cafecoffeeapp.domain.model

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val detailedDescription: String = "",
    val price: Double,
    val imageRes: Int,
    val imageUrl: String = "",
    val rating: Double = 4.8,
    val reviewsCount: Int = 120,
    val isFavorite: Boolean = false
)
