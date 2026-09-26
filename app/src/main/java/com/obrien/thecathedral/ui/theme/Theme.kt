package com.obrien.thecathedral.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Deep bronze-gold: legible accent for the light (parchment) theme.
private val LightGold = Color(0xFF8C6D1F)

private val DarkColorScheme = darkColorScheme(
    primary = CathedralGold,
    onPrimary = MonasteryBlack,
    secondary = Bronze,
    onSecondary = Parchment,
    background = MonasteryBlack,
    surface = Color(0xFF242424),
    onBackground = Parchment,
    onSurface = Parchment,
    surfaceVariant = MutedStone,
    onSurfaceVariant = MonasteryBlack,
    surfaceTint = CathedralGold
)

private val LightColorScheme = lightColorScheme(
    primary = LightGold,
    onPrimary = Color(0xFFFFFDF5),
    secondary = Bronze,
    onSecondary = Parchment,
    background = Parchment,
    surface = Color(0xFFFFFBF2),
    onBackground = Color(0xFF2A2419),
    onSurface = Color(0xFF2A2419),
    surfaceVariant = MutedStone,
    onSurfaceVariant = Color(0xFF2A2419),
    surfaceTint = LightGold
)

enum class ThemeMode { SYSTEM, DARK, LIGHT }

@Composable
fun TheCathedralTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val isDark = darkTheme ?: when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val cathedralColors = if (isDark) DarkCathedralColors else LightCathedralColors

    CompositionLocalProvider(LocalCathedralColors provides cathedralColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
