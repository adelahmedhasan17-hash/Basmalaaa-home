package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.CartItem
import com.example.model.Coupon
import com.example.model.PaymentMethod
import com.example.model.ShippingInfo
import com.example.model.UserProfile
import com.example.ui.theme.BasmalaBlack
import com.example.ui.theme.BasmalaDarkGold
import com.example.ui.theme.BasmalaDiscountRed
import com.example.ui.theme.BasmalaGold
import com.example.ui.theme.BasmalaLightGold
import com.example.ui.theme.BasmalaSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutSheet(
    cartItems: List<CartItem>,
    appliedCoupon: Coupon?,
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onConfirmOrder: (ShippingInfo, PaymentMethod) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentStep by remember { mutableIntStateOf(1) } // 1: Shipping, 2: Payment, 3: Review

    // Step 1 State: Shipping Info
    var fullName by remember { mutableStateOf(userProfile.name) }
    var phone by remember { mutableStateOf(userProfile.phone) }
    var governorate by remember { mutableStateOf("القاهرة") }
    var detailedAddress by remember { mutableStateOf(userProfile.address) }
    var notes by remember { mutableStateOf("") }
    var governorateExpanded by remember { mutableStateOf(false) }

    var step1Error by remember { mutableStateOf<String?>(null) }

    // Step 2 State: Payment Method
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }

    // Cost calculations
    val subtotal = cartItems.sumOf { it.totalPrice }
    val isFreeShipping = appliedCoupon?.isFreeShipping == true || subtotal >= 1000.0
    val shippingFee = if (subtotal == 0.0 || isFreeShipping) 0.0 else 45.0
    val discountAmount = when {
        appliedCoupon?.discountPercent != null && appliedCoupon.discountPercent > 0 -> subtotal * appliedCoupon.discountPercent
        appliedCoupon?.discountFixed != null && appliedCoupon.discountFixed > 0 -> minOf(appliedCoupon.discountFixed, subtotal)
        else -> 0.0
    }
    val finalTotal = maxOf(0.0, subtotal + shippingFee - discountAmount)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("checkout_sheet")
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💳 إتمام الشراء والدفع",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Step Indicator Header: 1️⃣ بيانات الشحن  ->  2️⃣ طريقة الدفع  ->  3️⃣ تأكيد الطلب
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepIndicatorItem(stepNumber = 1, title = "الشحن", isActive = currentStep == 1, isCompleted = currentStep > 1)
                Text(text = "•", color = Color.Gray)
                StepIndicatorItem(stepNumber = 2, title = "الدفع", isActive = currentStep == 2, isCompleted = currentStep > 2)
                Text(text = "•", color = Color.Gray)
                StepIndicatorItem(stepNumber = 3, title = "التأكيد", isActive = currentStep == 3, isCompleted = false)
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (currentStep) {
                1 -> {
                    // STEP 1: بيانات الشحن
                    Text(
                        text = "1️⃣ بيانات الشحن والتوصيل:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            step1Error = null
                        },
                        label = { Text("الاسم الكامل *") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BasmalaGold)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            step1Error = null
                        },
                        label = { Text("رقم الموبايل (مثال: 01012345678) *") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_phone_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BasmalaGold)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Governorate dropdown
                    ExposedDropdownMenuBox(
                        expanded = governorateExpanded,
                        onExpandedChange = { governorateExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = governorate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("المحافظة *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = governorateExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("checkout_governorate_dropdown"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BasmalaGold)
                        )
                        ExposedDropdownMenu(
                            expanded = governorateExpanded,
                            onDismissRequest = { governorateExpanded = false }
                        ) {
                            SampleData.governorates.forEach { gov ->
                                DropdownMenuItem(
                                    text = { Text(gov) },
                                    onClick = {
                                        governorate = gov
                                        governorateExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = detailedAddress,
                        onValueChange = {
                            detailedAddress = it
                            step1Error = null
                        },
                        label = { Text("العنوان بالتفصيل (المنطقة، الشارع، رقم العمارة، الشقة) *") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_address_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BasmalaGold)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات المندوب (اختياري)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BasmalaGold)
                    )

                    if (step1Error != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = step1Error ?: "",
                            color = BasmalaDiscountRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                step1Error = "يرجى كتابة الاسم الكامل للمستلم"
                            } else if (phone.isBlank()) {
                                step1Error = "يرجى كتابة رقم الموبايل للتواصل"
                            } else if (detailedAddress.isBlank()) {
                                step1Error = "يرجى إدخال العنوان بالتفصيل"
                            } else {
                                step1Error = null
                                currentStep = 2
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BasmalaBlack,
                            contentColor = BasmalaGold
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("step1_next_button")
                    ) {
                        Text(text = "المتابعة لطريقة الدفع ←", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                2 -> {
                    // STEP 2: طريقة الدفع
                    Text(
                        text = "2️⃣ اختر طريقة الدفع:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PaymentMethod.values().forEach { method ->
                        val isSelected = method == selectedPaymentMethod
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) BasmalaGold.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) BasmalaGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedPaymentMethod = method }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPaymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = BasmalaGold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = method.icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = method.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = method.subtitle,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { currentStep = 1 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("→ السابق")
                        }

                        Button(
                            onClick = { currentStep = 3 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BasmalaBlack,
                                contentColor = BasmalaGold
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.5f).height(48.dp).testTag("step2_next_button")
                        ) {
                            Text("مراجعة الطلب ←", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                3 -> {
                    // STEP 3: تأكيد الطلب
                    Text(
                        text = "3️⃣ مراجعة وتأكيد الطلب:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Shipping Summary Card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "📍 عنوان التوصيل:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$fullName - $phone", fontSize = 12.sp)
                            Text(text = "$governorate - $detailedAddress", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (notes.isNotBlank()) {
                                Text(text = "ملاحظات: $notes", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "طريقة الدفع: ${selectedPaymentMethod.title} ${selectedPaymentMethod.icon}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = BasmalaDarkGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Items Summary
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "📦 المنتجات (${cartItems.size}):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            cartItems.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.quantity}x ${item.product.title} (${item.selectedColor})",
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${item.totalPrice.toInt()} ج.م",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Totals
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "المجموع الفرعي:", fontSize = 13.sp)
                                Text(text = "${subtotal.toInt()} ج.م", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "الشحن:", fontSize = 13.sp)
                                if (isFreeShipping) {
                                    Text(text = "مجاني 🎁", color = BasmalaSuccess, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                } else {
                                    Text(text = "45 ج.م", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (discountAmount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "الخصم:", color = BasmalaDiscountRed, fontSize = 13.sp)
                                    Text(text = "-${discountAmount.toInt()} ج.م", color = BasmalaDiscountRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "الإجمالي المطلوب سداده:", fontWeight = FontWeight.Black, fontSize = 15.sp)
                                Text(
                                    text = "${finalTotal.toInt()} ج.م",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = BasmalaGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { currentStep = 2 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("→ السابق")
                        }

                        Button(
                            onClick = {
                                onConfirmOrder(
                                    ShippingInfo(
                                        fullName = fullName,
                                        phone = phone,
                                        governorate = governorate,
                                        detailedAddress = detailedAddress,
                                        notes = notes
                                    ),
                                    selectedPaymentMethod
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BasmalaGold,
                                contentColor = BasmalaBlack
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.8f).height(48.dp).testTag("confirm_order_button")
                        ) {
                            Text(
                                text = "تأكيد الطلب الآن 📦",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun StepIndicatorItem(
    stepNumber: Int,
    title: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    when {
                        isCompleted -> BasmalaSuccess
                        isActive -> BasmalaGold
                        else -> Color.Gray
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Text(
                    text = "$stepNumber",
                    color = if (isActive) BasmalaBlack else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp,
            color = if (isActive) BasmalaDarkGold else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
