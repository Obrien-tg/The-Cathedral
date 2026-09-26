package com.obrien.thelantern.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LumiLavender,
    onPrimary = TwilightIndigo,
    secondary = LumiBlue,
    onSecondary = LumiCream,
    tertiary = LumiPink,
    background = TwilightIndigo,
    surface = TwilightSurface,
    onBackground = LumiCream,
    onSurface = LumiCream,
    surfaceVariant = Color(0xFF3A3450),
    onSurfaceVariant = LumiCream,
    surfaceTint = LumiYellow,
    error = Color(0xFFFFB7B2),
    onError = TwilightIndigo
)

private val LightColorScheme = lightColorScheme(
    primary = LumiPurple,
    onPrimary = Color.White,
    secondary = LumiPink,
    onSecondary = LumiCharcoal,
    tertiary = LumiMint,
    background = LumiCream,
    surface = LumiPeachWhite,
    onBackground = LumiCharcoal,
    onSurface = LumiCharcoal,
    surfaceVariant = Color(0xFFFFF0EB),
    onSurfaceVariant = LumiCharcoal,
    surfaceTint = LumiYellow,
    error = Color(0xFFFFADAD),
    onError = Color.White
)

@Composable
fun LumiTheme(
    darkTheme: Boolean = false, // Always Lumi Daylight by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
