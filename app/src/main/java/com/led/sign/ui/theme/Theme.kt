package com.led.sign.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// MD3 Seed Colors
val SeedColors = listOf(
    Color(0xFF006D3E) to "绿色",
    Color(0xFF0061A4) to "蓝色",
    Color(0xFF904D00) to "橙色",
    Color(0xFFBA1A1A) to "红色",
    Color(0xFF6750A4) to "紫色",
    Color(0xFF006A6A) to "青色",
    Color(0xFF3F5AA9) to "靛蓝",
    Color(0xFF8C4D1A) to "棕色",
    Color(0xFF386A20) to "翠绿",
    Color(0xFF8B4586) to "紫红",
    Color(0xFF006590) to "天蓝",
    Color(0xFF7D5700) to "金色",
)

@Composable
fun LEDSignTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    seedColor: Color = SeedColors[0].first,
    dynamicColor: Boolean = false,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // 动态颜色 (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        // 深色模式
        darkTheme -> {
            if (highContrast) {
                darkColorScheme(
                    primary = seedColor,
                    onPrimary = Color.White,
                    primaryContainer = seedColor,
                    onPrimaryContainer = Color.Black,
                    secondary = seedColor.copy(alpha = 0.9f),
                    onSecondary = Color.White,
                    background = Color.Black,
                    onBackground = Color.White,
                    surface = Color.Black,
                    onSurface = Color.White,
                    surfaceVariant = Color(0xFF1A1A1A),
                    onSurfaceVariant = Color.White,
                    outline = Color(0xFFCCCCCC),
                )
            } else {
                darkColorScheme(
                    primary = seedColor,
                    onPrimary = Color.White,
                    primaryContainer = seedColor.copy(alpha = 0.3f),
                    onPrimaryContainer = Color.White,
                    secondary = seedColor.copy(alpha = 0.8f),
                    onSecondary = Color.White,
                    background = Color(0xFF1C1B1F),
                    onBackground = Color(0xFFE6E1E5),
                    surface = Color(0xFF1C1B1F),
                    onSurface = Color(0xFFE6E1E5),
                    surfaceVariant = Color(0xFF2D2D2D),
                    onSurfaceVariant = Color(0xFFCAC4D0),
                    surfaceContainerHigh = Color(0xFF363636),
                )
            }
        }
        // 浅色模式
        else -> {
            if (highContrast) {
                lightColorScheme(
                    primary = seedColor,
                    onPrimary = Color.White,
                    primaryContainer = seedColor,
                    onPrimaryContainer = Color.White,
                    secondary = seedColor,
                    onSecondary = Color.White,
                    background = Color.White,
                    onBackground = Color.Black,
                    surface = Color.White,
                    onSurface = Color.Black,
                    surfaceVariant = Color(0xFFF0F0F0),
                    onSurfaceVariant = Color.Black,
                    outline = Color(0xFF333333),
                )
            } else {
                lightColorScheme(
                    primary = seedColor,
                    onPrimary = Color.White,
                    primaryContainer = seedColor.copy(alpha = 0.15f),
                    onPrimaryContainer = seedColor,
                    secondary = seedColor.copy(alpha = 0.7f),
                    onSecondary = Color.White,
                    background = Color(0xFFFFFBFE),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color(0xFFFFFBFE),
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFF5F5F5),
                    onSurfaceVariant = Color(0xFF49454F),
                    surfaceContainerHigh = Color(0xFFE8E8E8),
                )
            }
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
