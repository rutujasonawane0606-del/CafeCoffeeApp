package com.example.cafecoffeeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cafecoffeeapp.domain.model.Product

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val productId: Int,
    val name: String,
    val category: String,
    val description: String,
    val detailedDescription: String,
    val price: Double,
    val imageRes: Int,
    val rating: Double,
    val reviewsCount: Int
) {
    fun toProduct(): Product {
        return Product(
            id = productId,
            name = name,
            category = category,
            description = description,
            detailedDescription = detailedDescription,
            price = price,
            imageRes = imageRes,
            rating = rating,
            reviewsCount = reviewsCount,
            isFavorite = true
        )
    }

    companion object {
        fun fromProduct(product: Product): FavoriteEntity {
            return FavoriteEntity(
                productId = product.id,
                name = product.name,
                category = product.category,
                description = product.description,
                detailedDescription = product.detailedDescription,
                price = product.price,
                imageRes = product.imageRes,
                rating = product.rating,
                reviewsCount = product.reviewsCount
            )
        }
    }
}
