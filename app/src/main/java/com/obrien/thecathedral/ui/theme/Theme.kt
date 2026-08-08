package com.obrien.thecathedral.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CathedralGold,
    onPrimary = MonasteryBlack,
    secondary = Bronze,
    onSecondary = Parchment,
    background = MonasteryBlack,
    surface = MonasteryBlack,
    onBackground = Parchment,
    onSurface = Parchment,
    surfaceVariant = MutedStone,
    onSurfaceVariant = MonasteryBlack
)

private val LightColorScheme = lightColorScheme(
    primary = CathedralGold,
    onPrimary = MonasteryBlack,
    secondary = Bronze,
    onSecondary = Parchment,
    background = Parchment,
    surface = Parchment,
    onBackground = MonasteryBlack,
    onSurface = MonasteryBlack
)

@Composable
fun TheCathedralTheme(
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
