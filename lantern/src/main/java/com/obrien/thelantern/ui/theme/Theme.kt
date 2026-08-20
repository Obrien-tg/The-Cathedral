package com.obrien.thelantern.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LanternViolet,
    onPrimary = LanternNight,
    secondary = LanternBlue,
    onSecondary = LanternText,
    tertiary = LanternPink,
    background = LanternNight,
    surface = LanternSurface,
    onBackground = LanternText,
    onSurface = LanternText,
    surfaceVariant = LanternMuted,
    onSurfaceVariant = LanternNight,
    surfaceTint = LanternGold
)

private val LightColorScheme = lightColorScheme(
    primary = LanternViolet,
    onPrimary = LanternNight,
    secondary = LanternBlue,
    onSecondary = LanternText,
    background = LanternText,
    surface = LanternText,
    onBackground = LanternNight,
    onSurface = LanternNight
)

@Composable
fun TheLanternTheme(
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
