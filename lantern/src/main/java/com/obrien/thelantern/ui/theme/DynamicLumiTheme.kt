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
    val todayColor = try {
        Color(android.graphics.Color.parseColor("#$todayColorHex"))
    } catch (_: Exception) {
        LumiPurple
    }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = todayColor,
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
            primary = todayColor,
            onPrimary = Color.White,
            secondary = LumiPink,
            onSecondary = LumiCharcoal,
            tertiary = LumiMint,
            background = LumiCream,
            surface = LumiPeachWhite,
            onBackground = LumiCharcoal,
            onSurface = LumiCharcoal,
            surfaceTint = LumiYellow,
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
