package com.example.cafecoffeeapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cafecoffeeapp.data.local.dao.CartDao
import com.example.cafecoffeeapp.data.local.dao.FavoriteDao
import com.example.cafecoffeeapp.data.local.dao.OrderDao
import com.example.cafecoffeeapp.data.local.entity.CartItemEntity
import com.example.cafecoffeeapp.data.local.entity.FavoriteEntity
import com.example.cafecoffeeapp.data.local.entity.OrderEntity

@Database(
    entities = [CartItemEntity::class, FavoriteEntity::class, OrderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cafe_coffee_app.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
