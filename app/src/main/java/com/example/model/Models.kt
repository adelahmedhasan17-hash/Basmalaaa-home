package com.example.model

data class Product(
    val id: Int,
    val icon: String,
    val cat: String,
    val title: String,
    val price: Double,
    val old: Double,
    val colors: List<String>,
    val rating: Double,
    val reviewsCount: Int,
    val shortDesc: String,
    val fullDesc: String,
    val specs: Map<String, String>,
    val reviews: List<CustomerReview>,
    val inStock: Boolean = true,
    val badge: String? = null,
    val isDealOfWeek: Boolean = false,
    val isBestSeller: Boolean = false,
    val isNewArrival: Boolean = false
) {
    val discountPercent: Int
        get() = if (old > price) (((old - price) / old) * 100).toInt() else 0
}

data class CustomerReview(
    val id: String,
    val reviewerName: String,
    val city: String,
    val rating: Double,
    val date: String,
    val comment: String
)

data class CartItem(
    val product: Product,
    val selectedColor: String,
    val quantity: Int
) {
    val totalPrice: Double
        get() = product.price * quantity
}

enum class PaymentMethod(val title: String, val subtitle: String, val icon: String) {
    CASH("كاش عند الاستلام", "الدفع نقداً عند استلام الشحنة لباب بيتك", "💵"),
    CARD("بطاقة ائتمانية", "فيزا / ماستركارد / ميزة مع تشفير آمن 100%", "💳"),
    WALLET("محفظة إلكترونية", "فودافون كاش / أورنج كاش / إي آند / إنستاباي", "📱")
}

data class ShippingInfo(
    val fullName: String = "",
    val phone: String = "",
    val governorate: String = "القاهرة",
    val detailedAddress: String = "",
    val notes: String = ""
)

data class Order(
    val id: String,
    val items: List<CartItem>,
    val shippingInfo: ShippingInfo,
    val paymentMethod: PaymentMethod,
    val subtotal: Double,
    val shippingCost: Double,
    val discount: Double,
    val total: Double,
    val couponCode: String?,
    val date: String,
    val status: String = "قيد التجهيز"
)

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val isLoggedIn: Boolean = false,
    val rememberMe: Boolean = true
)

data class Coupon(
    val code: String,
    val title: String,
    val description: String,
    val discountPercent: Double = 0.0,
    val discountFixed: Double = 0.0,
    val isFreeShipping: Boolean = false
)
