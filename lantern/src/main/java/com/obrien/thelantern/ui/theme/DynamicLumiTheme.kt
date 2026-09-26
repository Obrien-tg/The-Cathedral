package com.obrien.thelantern.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.obrien.core.data.DataStoreManager

@Composable
fun DynamicLumiTheme(
    dataStoreManager: DataStoreManager,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val todayColorHex by dataStoreManager.getTodayColor().collectAsState(initial = "FFB3C6")
    val rawColor = try {
        Color(android.graphics.Color.parseColor("#$todayColorHex"))
    } catch (_: Exception) {
        LumiPurple
    }

    // Darken for readability in light mode
    val primaryColor = rawColor.darken(0.5f)
    val secondaryColor = LumiBlue.darken(0.7f) // LumiBlue is A5D8FF

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = rawColor, 
            onPrimary = TwilightIndigo,
            secondary = LumiBlue,
            onSecondary = LumiCream,
            tertiary = LumiPink,
            background = TwilightIndigo,
            surface = TwilightSurface,
            onBackground = LumiCream,
            onSurface = LumiCream,
            surfaceTint = LumiYellow,
            error = Color(0xFFFFB7B2),
            onError = TwilightIndigo
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            secondary = rawColor,    
            onSecondary = LumiCharcoal,
            tertiary = LumiMint,
            background = LumiCream,
            surface = LumiPeachWhite,
            onBackground = LumiCharcoal,
            onSurface = LumiCharcoal,
            surfaceTint = rawColor.copy(alpha = 0.3f),
            error = Color(0xFFFFADAD),
            onError = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private fun Color.darken(factor: Float): Color {
    return this.copy(
        red = (this.red * factor).coerceIn(0f, 1f),
        green = (this.green * factor).coerceIn(0f, 1f),
        blue = (this.blue * factor).coerceIn(0f, 1f)
    )
}
