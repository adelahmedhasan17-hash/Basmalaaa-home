package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.UserProfile
import com.example.ui.theme.BasmalaBlack
import com.example.ui.theme.BasmalaDarkGold
import com.example.ui.theme.BasmalaDiscountRed
import com.example.ui.theme.BasmalaGold

@Composable
fun BasmalaTopBar(
    cartCount: Int,
    wishlistCount: Int,
    userProfile: UserProfile,
    isDarkMode: Boolean,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onAccountClick: () -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var logoRotated by remember { mutableStateOf(false) }
    val logoRotation by animateFloatAsState(
        targetValue = if (logoRotated) 360f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "logo_rotation"
    )

    Surface(
        color = BasmalaBlack,
        tonalElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main App Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Logo (Interactive spinning gold circle + "ب" + "بسمله هوم")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("brand_logo")
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { logoRotated = !logoRotated }
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .rotate(logoRotation)
                            .background(BasmalaGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ب",
                            color = BasmalaBlack,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "بسمله ",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "هوم",
                            color = BasmalaGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }
                }

                // Action Icons Row: Search, DarkMode, Wishlist, Cart, Account
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Search Button
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier.testTag("search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = Color.White
                        )
                    }

                    // Dark Mode Toggle Button
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "الوضع الليلي",
                            tint = if (isDarkMode) BasmalaGold else Color.White
                        )
                    }

                    // Wishlist Button with Badge
                    BadgedBox(
                        badge = {
                            if (wishlistCount > 0) {
                                Badge(
                                    containerColor = BasmalaDiscountRed,
                                    contentColor = Color.White
                                ) {
                                    Text(text = "$wishlistCount", fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onWishlistClick,
                            modifier = Modifier.testTag("wishlist_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "المفضلة",
                                tint = if (wishlistCount > 0) BasmalaDiscountRed else Color.White
                            )
                        }
                    }

                    // Cart Button with Badge
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(
                                    containerColor = BasmalaGold,
                                    contentColor = BasmalaBlack
                                ) {
                                    Text(
                                        text = "$cartCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onCartClick,
                            modifier = Modifier.testTag("cart_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "السلة",
                                tint = BasmalaGold
                            )
                        }
                    }

                    // Account Button with Avatar/Greeting
                    IconButton(
                        onClick = onAccountClick,
                        modifier = Modifier.testTag("account_button")
                    ) {
                        if (userProfile.isLoggedIn && userProfile.name.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(BasmalaDarkGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userProfile.name.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "حسابي",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Scrollable Category Tabs Bar
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SampleData.categories.forEach { category ->
                    val isSelected = category.id == selectedCategoryId
                    val chipBg = if (isSelected) BasmalaGold else Color(0xFF1E1E1E)
                    val chipText = if (isSelected) BasmalaBlack else Color.White

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .testTag("category_chip_${category.id}")
                            .clip(RoundedCornerShape(20.dp))
                            .background(chipBg)
                            .clickable { onCategorySelected(category.id) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = category.icon,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = category.name,
                            color = chipText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
