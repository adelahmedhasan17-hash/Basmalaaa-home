package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val selectedColor: String,
    val quantity: Int
)

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey val productId: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderRecordEntity(
    @PrimaryKey val id: String,
    val date: String,
    val total: Double,
    val itemsCount: Int,
    val status: String,
    val detailsSummary: String,
    val paymentMethod: String,
    val shippingAddress: String
)

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val isLoggedIn: Boolean,
    val rememberMe: Boolean
)
