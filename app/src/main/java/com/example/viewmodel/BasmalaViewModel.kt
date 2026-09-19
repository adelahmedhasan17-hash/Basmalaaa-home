package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BasmalaRepository
import com.example.data.SampleData
import com.example.data.db.AppDatabase
import com.example.data.db.OrderRecordEntity
import com.example.model.CartItem
import com.example.model.Coupon
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.ShippingInfo
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BasmalaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BasmalaRepository(AppDatabase.getDatabase(application))

    val allProducts: List<Product> = repository.allProducts
    val dealsOfWeek: List<Product> = allProducts.filter { it.isDealOfWeek }
    val bestSellers: List<Product> = allProducts.filter { it.isBestSeller }
    val newArrivals: List<Product> = allProducts.filter { it.isNewArrival }

    // Category & Search Filtering
    private val _selectedCategoryId = MutableStateFlow("all")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active modals and sheets
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _isCartOpen = MutableStateFlow(false)
    val isCartOpen: StateFlow<Boolean> = _isCartOpen.asStateFlow()

    private val _isCheckoutOpen = MutableStateFlow(false)
    val isCheckoutOpen: StateFlow<Boolean> = _isCheckoutOpen.asStateFlow()

    private val _isWishlistOpen = MutableStateFlow(false)
    val isWishlistOpen: StateFlow<Boolean> = _isWishlistOpen.asStateFlow()

    private val _isAuthOpen = MutableStateFlow(false)
    val isAuthOpen: StateFlow<Boolean> = _isAuthOpen.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    private val _isAboutOpen = MutableStateFlow(false)
    val isAboutOpen: StateFlow<Boolean> = _isAboutOpen.asStateFlow()

    private val _isContactOpen = MutableStateFlow(false)
    val isContactOpen: StateFlow<Boolean> = _isContactOpen.asStateFlow()

    private val _latestOrderSuccess = MutableStateFlow<OrderRecordEntity?>(null)
    val latestOrderSuccess: StateFlow<OrderRecordEntity?> = _latestOrderSuccess.asStateFlow()

    private val _trackingOrder = MutableStateFlow<OrderRecordEntity?>(null)
    val trackingOrder: StateFlow<OrderRecordEntity?> = _trackingOrder.asStateFlow()

    // Coupon & Toast Feedback
    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    private val _couponMessage = MutableStateFlow<String?>(null)
    val couponMessage: StateFlow<String?> = _couponMessage.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Database reactive streams
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistIds: StateFlow<Set<Int>> = repository.wishlistProductIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val orderHistory: StateFlow<List<OrderRecordEntity>> = repository.orderHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setCategoryId(id: String) {
        _selectedCategoryId.value = id
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
    }

    fun closeProductDetail() {
        _selectedProduct.value = null
    }

    fun openCart() {
        _isCartOpen.value = true
    }

    fun closeCart() {
        _isCartOpen.value = false
    }

    fun openCheckout() {
        _isCartOpen.value = false
        _isCheckoutOpen.value = true
    }

    fun closeCheckout() {
        _isCheckoutOpen.value = false
    }

    fun openWishlist() {
        _isWishlistOpen.value = true
    }

    fun closeWishlist() {
        _isWishlistOpen.value = false
    }

    fun openAuth() {
        _isAuthOpen.value = true
    }

    fun closeAuth() {
        _isAuthOpen.value = false
    }

    fun openSearch() {
        _isSearchOpen.value = true
    }

    fun closeSearch() {
        _isSearchOpen.value = false
    }

    fun openAbout() {
        _isAboutOpen.value = true
    }

    fun closeAbout() {
        _isAboutOpen.value = false
    }

    fun openContact() {
        _isContactOpen.value = true
    }

    fun closeContact() {
        _isContactOpen.value = false
    }

    fun dismissOrderSuccess() {
        _latestOrderSuccess.value = null
    }

    fun trackOrder(order: OrderRecordEntity) {
        _trackingOrder.value = order
    }

    fun dismissOrderTracking() {
        _trackingOrder.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun addToCart(product: Product, selectedColor: String? = null, quantity: Int = 1) {
        val color = selectedColor ?: product.colors.firstOrNull() ?: "افتراضي"
        viewModelScope.launch {
            repository.addToCart(product.id, color, quantity)
            _toastMessage.value = "تمت إضافة ${product.title} إلى السلة 🛍️"
        }
    }

    fun updateCartQuantity(productId: Int, color: String, newQty: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, color, newQty)
            if (newQty <= 0) {
                _toastMessage.value = "تم حذف المنتج من السلة"
            }
        }
    }

    fun removeFromCart(productId: Int, color: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId, color)
            _toastMessage.value = "تم حذف المنتج من السلة"
        }
    }

    fun toggleWishlist(product: Product) {
        val current = wishlistIds.value.contains(product.id)
        viewModelScope.launch {
            repository.toggleWishlist(product.id, current)
            _toastMessage.value = if (!current) "تمت إضافة ${product.title} إلى المفضلة ❤️" else "تمت إزالة المنتج من المفضلة"
        }
    }

    fun applyCoupon(code: String) {
        val cleanCode = code.trim().uppercase()
        val found = SampleData.validCoupons.find { it.code.equals(cleanCode, ignoreCase = true) }
        if (found != null) {
            _appliedCoupon.value = found
            _couponMessage.value = "تم تطبيق الكوبون بنجاح: ${found.title} ✅"
            _toastMessage.value = "تم تطبيق كود الخصم بنجاح 🎉"
        } else {
            _couponMessage.value = "كود الخصم غير صحيح أو منتهي الصلاحية ❌"
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _couponMessage.value = null
        _toastMessage.value = "تم إلغاء كود الخصم"
    }

    fun loginUser(name: String, email: String, phone: String, address: String, rememberMe: Boolean) {
        viewModelScope.launch {
            val profile = UserProfile(
                name = name.ifBlank { "عميل بسمله هوم" },
                email = email,
                phone = phone,
                address = address,
                isLoggedIn = true,
                rememberMe = rememberMe
            )
            repository.saveUserProfile(profile)
            _toastMessage.value = "أهلاً بك يا ${profile.name} في بسمله هوم ✨"
            _isAuthOpen.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutUser()
            _toastMessage.value = "تم تسجيل الخروج بنجاح"
        }
    }

    fun confirmOrder(
        shippingInfo: ShippingInfo,
        paymentMethod: PaymentMethod
    ) {
        val currentItems = cartItems.value
        if (currentItems.isEmpty()) return

        val subtotal = currentItems.sumOf { it.totalPrice }
        val coupon = appliedCoupon.value
        val shippingFee = if (coupon?.isFreeShipping == true || subtotal >= 1000.0) 0.0 else 45.0
        val discount = when {
            coupon?.discountPercent != null && coupon.discountPercent > 0 -> subtotal * coupon.discountPercent
            coupon?.discountFixed != null && coupon.discountFixed > 0 -> minOf(coupon.discountFixed, subtotal)
            else -> 0.0
        }
        val finalTotal = maxOf(0.0, subtotal + shippingFee - discount)

        viewModelScope.launch {
            val orderId = repository.placeOrder(
                items = currentItems,
                shippingInfo = shippingInfo,
                paymentMethod = paymentMethod,
                total = finalTotal
            )
            val createdOrder = OrderRecordEntity(
                id = orderId,
                date = "اليوم",
                total = finalTotal,
                itemsCount = currentItems.sumOf { it.quantity },
                status = "قيد التجهيز",
                detailsSummary = currentItems.joinToString(", ") { "${it.quantity}x ${it.product.title}" },
                paymentMethod = paymentMethod.title,
                shippingAddress = "${shippingInfo.governorate} - ${shippingInfo.detailedAddress}"
            )
            _latestOrderSuccess.value = createdOrder
            _isCheckoutOpen.value = false
            _appliedCoupon.value = null
            _couponMessage.value = null
            _toastMessage.value = "تم تأكيد طلبك بنجاح! رقم الطلب: $orderId 📦"
        }
    }

    fun getFilteredProducts(): List<Product> {
        val cat = selectedCategoryId.value
        val query = searchQuery.value.trim()

        var list = if (cat == "all") {
            allProducts
        } else {
            val catName = SampleData.categories.find { it.id == cat }?.name ?: ""
            allProducts.filter { it.cat == catName }
        }

        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.cat.contains(query, ignoreCase = true) ||
                        it.shortDesc.contains(query, ignoreCase = true)
            }
        }

        return list
    }
}
