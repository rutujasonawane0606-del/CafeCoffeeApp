package com.example.cafecoffeeapp.data.local

import android.content.Context
import com.example.cafecoffeeapp.data.local.dao.CartDao
import com.example.cafecoffeeapp.data.local.dao.FavoriteDao
import com.example.cafecoffeeapp.data.local.dao.OrderDao

class CafeCoffeeDatabaseProvider(context: Context) {

    private val localHelper = LocalDatabaseHelper.getInstance(context)

    val cartDao: CartDao
        get() = localHelper.cartDao

    val favoriteDao: FavoriteDao
        get() = localHelper.favoriteDao

    val orderDao: OrderDao
        get() = localHelper.orderDao

    companion object {
        @Volatile
        private var INSTANCE: CafeCoffeeDatabaseProvider? = null

        fun getInstance(context: Context): CafeCoffeeDatabaseProvider {
            return INSTANCE ?: synchronized(this) {
                val instance = CafeCoffeeDatabaseProvider(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
