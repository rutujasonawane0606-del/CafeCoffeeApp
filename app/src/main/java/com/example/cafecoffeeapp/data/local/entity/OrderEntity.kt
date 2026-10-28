package com.example.cafecoffeeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cafecoffeeapp.domain.model.CartItem
import com.example.cafecoffeeapp.domain.model.Order
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String,
    val timestamp: Long,
    val itemsJson: String,
    val subtotal: Double,
    val deliveryFee: Double,
    val taxes: Double,
    val totalAmount: Double,
    val deliveryAddress: String,
    val paymentMethod: String,
    val orderStatus: String
) {
    fun toOrder(): Order {
        val cartItems = mutableListOf<CartItem>()
        try {
            val jsonArray = JSONArray(itemsJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                cartItems.add(
                    CartItem(
                        id = obj.optLong("id", 0L),
                        productId = obj.optInt("productId", 0),
                        name = obj.optString("name", ""),
                        category = obj.optString("category", ""),
                        price = obj.optDouble("price", 0.0),
                        imageRes = obj.optInt("imageRes", 0),
                        size = obj.optString("size", "M"),
                        quantity = obj.optInt("quantity", 1)
                    )
                )
            }
        } catch (_: Exception) {
            // fallback
        }

        return Order(
            orderId = orderId,
            timestamp = timestamp,
            items = cartItems,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            taxes = taxes,
            totalAmount = totalAmount,
            deliveryAddress = deliveryAddress,
            paymentMethod = paymentMethod,
            orderStatus = orderStatus
        )
    }

    companion object {
        fun fromOrder(order: Order): OrderEntity {
            val jsonArray = JSONArray()
            order.items.forEach { item ->
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("productId", item.productId)
                    put("name", item.name)
                    put("category", item.category)
                    put("price", item.price)
                    put("imageRes", item.imageRes)
                    put("size", item.size)
                    put("quantity", item.quantity)
                }
                jsonArray.put(obj)
            }

            return OrderEntity(
                orderId = order.orderId,
                timestamp = order.timestamp,
                itemsJson = jsonArray.toString(),
                subtotal = order.subtotal,
                deliveryFee = order.deliveryFee,
                taxes = order.taxes,
                totalAmount = order.totalAmount,
                deliveryAddress = order.deliveryAddress,
                paymentMethod = order.paymentMethod,
                orderStatus = order.orderStatus
            )
        }
    }
}
