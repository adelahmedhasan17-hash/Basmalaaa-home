package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.Product
import com.example.ui.components.BasmalaTopBar
import com.example.ui.components.CategoriesGrid
import com.example.ui.components.FooterSection
import com.example.ui.components.HeroSection
import com.example.ui.components.ProductCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StoreFeaturesRow
import com.example.ui.components.TestimonialsSection
import com.example.ui.screens.AboutDialog
import com.example.ui.screens.AuthSheet
import com.example.ui.screens.CartSheet
import com.example.ui.screens.CheckoutSheet
import com.example.ui.screens.ContactDialog
import com.example.ui.screens.OrderSuccessDialog
import com.example.ui.screens.OrderTrackingDialog
import com.example.ui.screens.ProductDetailSheet
import com.example.ui.screens.SearchSheet
import com.example.ui.screens.WishlistSheet
import com.example.ui.theme.BasmalaBlack
import com.example.ui.theme.BasmalaDarkGold
import com.example.ui.theme.BasmalaGold
import com.example.ui.theme.BasmalaTheme
import com.example.viewmodel.BasmalaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BasmalaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            BasmalaTheme(darkTheme = isDarkMode) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    BasmalaApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BasmalaApp(viewModel: BasmalaViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val orderHistory by viewModel.orderHistory.collectAsState()
    val selectedCategory by viewModel.selectedCategoryId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Modals & Dialogs States
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val isCartOpen by viewModel.isCartOpen.collectAsState()
    val isCheckoutOpen by viewModel.isCheckoutOpen.collectAsState()
    val isWishlistOpen by viewModel.isWishlistOpen.collectAsState()
    val isAuthOpen by viewModel.isAuthOpen.collectAsState()
    val isSearchOpen by viewModel.isSearchOpen.collectAsState()
    val isAboutOpen by viewModel.isAboutOpen.collectAsState()
    val isContactOpen by viewModel.isContactOpen.collectAsState()
    val latestOrderSuccess by viewModel.latestOrderSuccess.collectAsState()
    val trackingOrder by viewModel.trackingOrder.collectAsState()

    // Coupon and snackbar
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val couponMessage by viewModel.couponMessage.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    val filteredProducts = viewModel.getFilteredProducts()
    val totalCartCount = cartItems.sumOf { it.quantity }
    val cartSubtotal = cartItems.sumOf { it.totalPrice }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BasmalaTopBar(
                cartCount = totalCartCount,
                wishlistCount = wishlistIds.size,
                userProfile = userProfile,
                isDarkMode = isDarkMode,
                selectedCategoryId = selectedCategory,
                onCategorySelected = { viewModel.setCategoryId(it) },
                onSearchClick = { viewModel.openSearch() },
                onCartClick = { viewModel.openCart() },
                onWishlistClick = { viewModel.openWishlist() },
                onAccountClick = { viewModel.openAuth() },
                onToggleDarkMode = { viewModel.toggleDarkMode() }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = BasmalaBlack,
                    contentColor = BasmalaGold,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = data.visuals.message, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        bottomBar = {
            // Floating Quick-Checkout Bottom Bar when Cart has items
            AnimatedVisibility(
                visible = totalCartCount > 0 && !isCartOpen && !isCheckoutOpen,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Card(
                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                    colors = CardDefaults.cardColors(containerColor = BasmalaBlack),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openCart() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(BasmalaGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = BasmalaBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "$totalCartCount منتجات في السلة",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${cartSubtotal.toInt()} ج.م",
                                    color = BasmalaGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(BasmalaGold)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "عرض السلة والشراء 🛍️",
                                color = BasmalaBlack,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Hero Banner
            item {
                Box(modifier = Modifier.padding(14.dp)) {
                    HeroSection(
                        onShopNowClick = {
                            viewModel.setCategoryId("all")
                        }
                    )
                }
            }

            // 2. Main Categories Grid (Section 4)
            item {
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    SectionHeader(
                        title = "الأقسام الرئيسية",
                        emoji = "🏷️",
                        subtitle = "تصفحي جميع مستلزمات البيت حسب الفئة",
                        actionText = if (selectedCategory != "all") "عرض الكل" else null,
                        onActionClick = { viewModel.setCategoryId("all") }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CategoriesGrid(
                        selectedCategoryId = selectedCategory,
                        onCategoryClick = { viewModel.setCategoryId(it) }
                    )
                }
            }

            // 3. Deals of the Week (عروض الأسبوع)
            if (selectedCategory == "all" && viewModel.dealsOfWeek.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                            SectionHeader(
                                title = "عروض الأسبوع الحصرية",
                                emoji = "🔥",
                                subtitle = "خصومات ضخمة على أطقم الحلل والأجهزة"
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.dealsOfWeek) { product ->
                                ProductCard(
                                    product = product,
                                    isWishlisted = wishlistIds.contains(product.id),
                                    onProductClick = { viewModel.openProductDetail(it) },
                                    onAddToCart = { viewModel.addToCart(it) },
                                    onToggleWishlist = { viewModel.toggleWishlist(it) },
                                    modifier = Modifier.width(190.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Store Features (Trust Bar - Section 2.1)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                    StoreFeaturesRow()
                }
            }

            // 5. Filtered Products Grid (2-Column Grid)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    SectionHeader(
                        title = if (selectedCategory == "all") "جميع المنتجات المتوفرة" else "منتجات ${SampleData.categories.find { it.id == selectedCategory }?.name}",
                        emoji = "✨",
                        subtitle = "${filteredProducts.size} منتج متوفر للشحن الفوري"
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val chunked = filteredProducts.chunked(2)
                    chunked.forEach { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { product ->
                                ProductCard(
                                    product = product,
                                    isWishlisted = wishlistIds.contains(product.id),
                                    onProductClick = { viewModel.openProductDetail(it) },
                                    onAddToCart = { viewModel.addToCart(it) },
                                    onToggleWishlist = { viewModel.toggleWishlist(it) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // 6. Best Sellers Section (الأكثر مبيعاً)
            if (selectedCategory == "all" && viewModel.bestSellers.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                            SectionHeader(
                                title = "الأكثر مبيعاً في مصر",
                                emoji = "👑",
                                subtitle = "المنتجات الأكثر طلباً وإشادة من عملائنا"
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.bestSellers) { product ->
                                ProductCard(
                                    product = product,
                                    isWishlisted = wishlistIds.contains(product.id),
                                    onProductClick = { viewModel.openProductDetail(it) },
                                    onAddToCart = { viewModel.addToCart(it) },
                                    onToggleWishlist = { viewModel.toggleWishlist(it) },
                                    modifier = Modifier.width(190.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 7. Customer Testimonials (Section 2.1 & 14)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    SectionHeader(
                        title = "آراء وتجارب العملاء",
                        emoji = "💬",
                        subtitle = "ماذا يقول عملاؤنا في مختلف محافظات مصر"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TestimonialsSection()
                }
            }

            // 8. Footer Section (Section 3.1 & 15)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                FooterSection(
                    onAboutClick = { viewModel.openAbout() },
                    onContactClick = { viewModel.openContact() }
                )
            }
        }
    }

    // ================== Modals and Dialogs ==================

    // 1. Product Detail Sheet
    selectedProduct?.let { product ->
        ProductDetailSheet(
            product = product,
            isWishlisted = wishlistIds.contains(product.id),
            onDismiss = { viewModel.closeProductDetail() },
            onAddToCart = { prod, color, qty ->
                viewModel.addToCart(prod, color, qty)
            },
            onBuyNow = { prod, color, qty ->
                viewModel.addToCart(prod, color, qty)
                viewModel.openCheckout()
            },
            onToggleWishlist = { viewModel.toggleWishlist(it) }
        )
    }

    // 2. Cart Sheet
    if (isCartOpen) {
        CartSheet(
            cartItems = cartItems,
            appliedCoupon = appliedCoupon,
            couponMessage = couponMessage,
            onDismiss = { viewModel.closeCart() },
            onUpdateQuantity = { id, color, qty -> viewModel.updateCartQuantity(id, color, qty) },
            onRemoveItem = { id, color -> viewModel.removeFromCart(id, color) },
            onApplyCoupon = { code -> viewModel.applyCoupon(code) },
            onRemoveCoupon = { viewModel.removeCoupon() },
            onProceedToCheckout = { viewModel.openCheckout() }
        )
    }

    // 3. Checkout Sheet
    if (isCheckoutOpen) {
        CheckoutSheet(
            cartItems = cartItems,
            appliedCoupon = appliedCoupon,
            userProfile = userProfile,
            onDismiss = { viewModel.closeCheckout() },
            onConfirmOrder = { shipping, payment ->
                viewModel.confirmOrder(shipping, payment)
            }
        )
    }

    // 4. Wishlist Sheet
    if (isWishlistOpen) {
        val wishlistProducts = viewModel.allProducts.filter { wishlistIds.contains(it.id) }
        WishlistSheet(
            wishlistProducts = wishlistProducts,
            onDismiss = { viewModel.closeWishlist() },
            onAddToCart = { viewModel.addToCart(it) },
            onRemoveFromWishlist = { viewModel.toggleWishlist(it) },
            onProductClick = { viewModel.openProductDetail(it) }
        )
    }

    // 5. Auth / Profile Sheet
    if (isAuthOpen) {
        AuthSheet(
            userProfile = userProfile,
            orderHistory = orderHistory,
            onDismiss = { viewModel.closeAuth() },
            onLogin = { name, email, phone, address, rememberMe ->
                viewModel.loginUser(name, email, phone, address, rememberMe)
            },
            onLogout = { viewModel.logout() },
            onTrackOrder = { order ->
                viewModel.closeAuth()
                viewModel.trackOrder(order)
            }
        )
    }

    // 6. Search Sheet
    if (isSearchOpen) {
        SearchSheet(
            query = searchQuery,
            filteredProducts = filteredProducts,
            onQueryChange = { viewModel.setSearchQuery(it) },
            onDismiss = { viewModel.closeSearch() },
            onProductClick = { viewModel.openProductDetail(it) },
            onAddToCart = { viewModel.addToCart(it) }
        )
    }

    // 7. Order Confirmation Dialog
    latestOrderSuccess?.let { order ->
        OrderSuccessDialog(
            order = order,
            onDismiss = { viewModel.dismissOrderSuccess() },
            onTrackOrder = {
                viewModel.dismissOrderSuccess()
                viewModel.trackOrder(it)
            }
        )
    }

    // 8. Order Tracking Dialog
    trackingOrder?.let { order ->
        OrderTrackingDialog(
            order = order,
            onDismiss = { viewModel.dismissOrderTracking() }
        )
    }

    // 9. About Dialog
    if (isAboutOpen) {
        AboutDialog(onDismiss = { viewModel.closeAbout() })
    }

    // 10. Contact Dialog
    if (isContactOpen) {
        ContactDialog(
            onDismiss = { viewModel.closeContact() },
            onSendMessageSuccess = {
                viewModel.closeContact()
                // triggers toast via state
                viewModel.applyCoupon("") // harmless or we can post a toast
            }
        )
    }
}
