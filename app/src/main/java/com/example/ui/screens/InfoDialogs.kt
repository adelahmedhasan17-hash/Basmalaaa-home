package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BasmalaBlack
import com.example.ui.theme.BasmalaDarkGold
import com.example.ui.theme.BasmalaGold
import com.example.ui.theme.BasmalaSuccess

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BasmalaBlack,
                    contentColor = BasmalaGold
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("إغلاق", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(BasmalaGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ب", color = BasmalaBlack, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "من نحن — بسمله هوم",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "“أن نكون المتجر الإلكتروني الأول للأدوات المنزلية في مصر، بتجربة تسوق سهلة وأسعار منافسة وجودة مضمونة.”",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BasmalaDarkGold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "تأسست بسمله هوم بهدف تقديم أفضل الأدوات المنزلية والأجهزة العصرية وأطقم النيش والسفرة لكل بيت مصري ولكل عروسة بتجهز. نضمن جودة المنتجات بنسبة 100% ونوفر خدمة توصيل سريعة ودفع آمن عند الاستلام.",
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "⭐ منتجات أصلية معتمدة مع ضمان حقيقي", fontSize = 11.sp)
                        Text(text = "🚚 شحن لكافة محافظات جمهورية مصر العربية", fontSize = 11.sp)
                        Text(text = "🔄 حق استبدال واسترجاع خلال 14 يوماً", fontSize = 11.sp)
                    }
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun ContactDialog(
    onDismiss: () -> Unit,
    onSendMessageSuccess: (String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var sentSuccess by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        sentSuccess = true
                        onSendMessageSuccess("تم استلام رسالتك وسيتم الرد خلال ساعات قليلة ✉️")
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BasmalaBlack,
                    contentColor = BasmalaGold
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (messageText.isNotBlank()) "إرسال الرسالة 📨" else "إغلاق", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            if (messageText.isNotBlank()) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("إلغاء")
                }
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = null,
                    tint = BasmalaGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "اتصل بنا وخدمة العملاء",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "فريق خدمة عملاء بسمله هوم متاح يومياً من 9 صباحاً حتى 10 مساءً للإجابة على استفساراتكم.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = BasmalaGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "الهاتف / واتساب: 01000000000", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = BasmalaGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "الإيميل: support@basmalahome.com", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BasmalaGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "المقر: القاهرة - مصر", fontSize = 12.sp)
                        }
                    }
                }

                if (sentSuccess) {
                    Text(
                        text = "تم إرسال رسالتك بنجاح! شكراً لتواصلك معنا.",
                        color = BasmalaSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                } else {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("اكتب استفسارك أو طلبك هنا...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3
                    )
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
