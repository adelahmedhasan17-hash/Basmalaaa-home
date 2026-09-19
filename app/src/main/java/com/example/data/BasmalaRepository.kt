package com.example.data

import com.example.data.db.AppDatabase
import com.example.data.db.CartEntity
import com.example.data.db.OrderRecordEntity
import com.example.data.db.UserEntity
import com.example.data.db.WishlistEntity
import com.example.model.CartItem
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.ShippingInfo
import com.example.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class BasmalaRepository(private val database: AppDatabase) {

    private val cartDao = database.cartDao()
    private val wishlistDao = database.wishlistDao()
    private val orderDao = database.orderDao()
    private val userDao = database.userDao()

    val allProducts: List<Product> = SampleData.products

    fun getProductById(id: Int): Product? = allProducts.find { it.id == id }

    val cartItems: Flow<List<CartItem>> = cartDao.getAllCartItems().map { entities ->
        entities.mapNotNull { entity ->
            val product = getProductById(entity.productId)
            if (product != null) {
                CartItem(
                    product = product,
                    selectedColor = entity.selectedColor,
                    quantity = entity.quantity
                )
            } else null
        }
    }

    val wishlistProductIds: Flow<Set<Int>> = wishlistDao.getAllWishlist().map { list ->
        list.map { it.productId }.toSet()
    }

    val userProfile: Flow<UserProfile> = userDao.getUser().map { entity ->
        if (entity != null) {
            UserProfile(
                name = entity.name,
                email = entity.email,
                phone = entity.phone,
                address = entity.address,
                isLoggedIn = entity.isLoggedIn,
                rememberMe = entity.rememberMe
            )
        } else {
            UserProfile()
        }
    }

    val orderHistory: Flow<List<OrderRecordEntity>> = orderDao.getAllOrders()

    suspend fun addToCart(productId: Int, color: String, quantity: Int = 1) {
        val existing = cartDao.findCartItem(productId, color)
        if (existing != null) {
            cartDao.updateQuantity(existing.id, existing.quantity + quantity)
        } else {
            cartDao.insertOrUpdate(
                CartEntity(
                    productId = productId,
                    selectedColor = color,
                    quantity = quantity
                )
            )
        }
    }

    suspend fun updateCartQuantity(productId: Int, color: String, newQty: Int) {
        if (newQty <= 0) {
            cartDao.deleteCartItem(productId, color)
        } else {
            val existing = cartDao.findCartItem(productId, color)
            if (existing != null) {
                cartDao.updateQuantity(existing.id, newQty)
            } else {
                cartDao.insertOrUpdate(
                    CartEntity(
                        productId = productId,
                        selectedColor = color,
                        quantity = newQty
                    )
                )
            }
        }
    }

    suspend fun removeFromCart(productId: Int, color: String) {
        cartDao.deleteCartItem(productId, color)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun toggleWishlist(productId: Int, currentIsWishlisted: Boolean) {
        if (currentIsWishlisted) {
            wishlistDao.removeFromWishlist(productId)
        } else {
            wishlistDao.addToWishlist(WishlistEntity(productId))
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userDao.saveUser(
            UserEntity(
                id = 1,
                name = profile.name,
                email = profile.email,
                phone = profile.phone,
                address = profile.address,
                isLoggedIn = profile.isLoggedIn,
                rememberMe = profile.rememberMe
            )
        )
    }

    suspend fun logoutUser() {
        userDao.clearUser()
    }

    suspend fun placeOrder(
        items: List<CartItem>,
        shippingInfo: ShippingInfo,
        paymentMethod: PaymentMethod,
        total: Double
    ): String {
        val orderNum = "BH-${Random.nextInt(100000, 999999)}"
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ar", "EG")).format(Date())
        val summary = items.joinToString(" + ") { "${it.quantity}x ${it.product.title}" }

        orderDao.insertOrder(
            OrderRecordEntity(
                id = orderNum,
                date = dateStr,
                total = total,
                itemsCount = items.sumOf { it.quantity },
                status = "قيد التجهيز",
                detailsSummary = summary,
                paymentMethod = paymentMethod.title,
                shippingAddress = "${shippingInfo.governorate} - ${shippingInfo.detailedAddress}"
            )
        )
        cartDao.clearCart()
        return orderNum
    }
}
