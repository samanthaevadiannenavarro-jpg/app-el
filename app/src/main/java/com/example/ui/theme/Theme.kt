package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Customer Floral Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = ElayaPinkPrimary,
    onPrimary = Color.White,
    primaryContainer = ElayaPinkLight,
    onPrimaryContainer = ElayaRoseDark,
    secondary = ElayaPinkSecondary,
    onSecondary = Color.White,
    tertiary = ElayaGoldAccent,
    background = ElayaBackground,
    surface = ElayaSurface,
    surfaceVariant = Color(0xFFFFF5F8),
    onBackground = ElayaTextPrimary,
    onSurface = ElayaTextPrimary,
    outline = ElayaBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = ElayaPinkSecondary,
    onPrimary = Color.Black,
    primaryContainer = ElayaRoseDark,
    onPrimaryContainer = ElayaPinkLight,
    secondary = ElayaPinkPrimary,
    background = Color(0xFF191216),
    surface = Color(0xFF231B20),
    onBackground = Color(0xFFF7EFF2),
    onSurface = Color(0xFFF7EFF2)
)

// Flower Shop Owner (Emerald & Slate) Color Scheme
private val OwnerColorScheme = lightColorScheme(
    primary = OwnerEmeraldDark,
    onPrimary = Color.White,
    primaryContainer = OwnerEmeraldLight,
    onPrimaryContainer = OwnerEmeraldDark,
    secondary = OwnerEmeraldPrimary,
    onSecondary = Color.White,
    tertiary = ElayaRoseDark,
    background = ElayaBackground,
    surface = Color.White,
    surfaceVariant = Color(0xFFF0FDF4),
    onBackground = ElayaTextPrimary,
    onSurface = ElayaTextPrimary,
    outline = ElayaBorder
)

// Admin Portal (Indigo & Slate) Color Scheme
private val AdminColorScheme = lightColorScheme(
    primary = AdminIndigoDark,
    onPrimary = Color.White,
    primaryContainer = AdminIndigoLight,
    onPrimaryContainer = AdminIndigoDark,
    secondary = AdminIndigoPrimary,
    onSecondary = Color.White,
    tertiary = Color(0xFFEC4899),
    background = ElayaBackground,
    surface = Color.White,
    surfaceVariant = Color(0xFFEEF2FF),
    onBackground = ElayaTextPrimary,
    onSurface = ElayaTextPrimary,
    outline = ElayaBorder
)

object ElayaGradients {
    val floralPrimary = Brush.linearGradient(
        colors = listOf(ElayaPinkPrimary, ElayaPinkSecondary)
    )

    val softBlush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF0F5), Color(0xFFFFFAFB))
    )

    val ownerMerchant = Brush.linearGradient(
        colors = listOf(OwnerEmeraldDark, OwnerEmeraldPrimary)
    )

    val adminPortal = Brush.linearGradient(
        colors = listOf(AdminIndigoDark, AdminIndigoPrimary)
    )

    val cardGlass = Brush.verticalGradient(
        colors = listOf(Color(0x33FFFFFF), Color(0x0DFFFFFF))
    )
}

@Composable
fun ElayaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ElayaOwnerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OwnerColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ElayaAdminTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AdminColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    ElayaTheme(darkTheme = darkTheme, content = content)
}
