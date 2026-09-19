package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BasmalaGold,
    onPrimary = BasmalaBlack,
    primaryContainer = BasmalaDarkGold,
    onPrimaryContainer = Color.White,
    secondary = BasmalaGold,
    onSecondary = BasmalaBlack,
    background = BasmalaDarkBackground,
    onBackground = BasmalaDarkTextPrimary,
    surface = BasmalaDarkSurface,
    onSurface = BasmalaDarkTextPrimary,
    surfaceVariant = BasmalaDarkCard,
    onSurfaceVariant = BasmalaDarkTextSecondary,
    outline = BasmalaDarkBorder,
    error = BasmalaDiscountRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BasmalaGold,
    onPrimary = BasmalaBlack,
    primaryContainer = BasmalaLightGold,
    onPrimaryContainer = BasmalaBlack,
    secondary = BasmalaBlack,
    onSecondary = Color.White,
    background = BasmalaLightGray,
    onBackground = BasmalaTextPrimary,
    surface = BasmalaWhite,
    onSurface = BasmalaTextPrimary,
    surfaceVariant = BasmalaLightGray,
    onSurfaceVariant = BasmalaTextSecondary,
    outline = BasmalaCardBorder,
    error = BasmalaDiscountRed,
    onError = Color.White
)

@Composable
fun BasmalaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
