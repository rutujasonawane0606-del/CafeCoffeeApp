package com.example.cafecoffeeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cafecoffeeapp.domain.model.CartItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Int,
    val name: String,
    val category: String,
    val price: Double,
    val imageRes: Int,
    val size: String,
    val quantity: Int
) {
    fun toDomain(): CartItem {
        return CartItem(
            id = id,
            productId = productId,
            name = name,
            category = category,
            price = price,
            imageRes = imageRes,
            size = size,
            quantity = quantity
        )
    }

    companion object {
        fun fromDomain(cartItem: CartItem): CartItemEntity {
            return CartItemEntity(
                id = cartItem.id,
                productId = cartItem.productId,
                name = cartItem.name,
                category = cartItem.category,
                price = cartItem.price,
                imageRes = cartItem.imageRes,
                size = cartItem.size,
                quantity = cartItem.quantity
            )
        }
    }
}
