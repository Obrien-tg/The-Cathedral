package com.obrien.thecathedral.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic colours for The Cathedral, beyond what Material's ColorScheme carries.
 * Access in composables via LocalCathedralColors.current.
 *
 * Phase 1 of the design refresh: the legacy top-level names in Color.kt are
 * kept as aliases pointing at the DARK palette, so existing screens compile
 * unchanged. Screens migrate to these tokens file-by-file in a later pass.
 */
@Immutable
data class CathedralColors(
    val gold: Color,              // primary accent
    val onGold: Color,            // text/icons placed on the accent
    val background: Color,
    val surface: Color,
    val surfaceGlass: Color,      // translucent card fill (fake glass)
    val glassBorder: Color,
    val onBackground: Color,      // primary text
    val onBackgroundMuted: Color, // secondary / placeholder text
    val activeGlow: Color         // active pillar highlight, pulses
)

val DarkCathedralColors = CathedralColors(
    gold = CathedralGold,
    onGold = MonasteryBlack,
    background = MonasteryBlack,
    surface = Color(0xFF242424),
    surfaceGlass = Color(0x1FFFFFFF),
    glassBorder = CathedralGold.copy(alpha = 0.20f),
    onBackground = Parchment,
    onBackgroundMuted = Parchment.copy(alpha = 0.60f),
    activeGlow = CathedralGold.copy(alpha = 0.35f)
)

val LightCathedralColors = CathedralColors(
    gold = Color(0xFF8C6D1F),     // deep bronze-gold — CathedralGold fails contrast on cream
    onGold = Color(0xFFFFFDF5),
    background = Parchment,
    surface = Color(0xFFFFFBF2),
    surfaceGlass = Color(0x40FFFFFF),
    glassBorder = Color(0xFF8C6D1F).copy(alpha = 0.25f),
    onBackground = Color(0xFF2A2419),
    onBackgroundMuted = Color(0xFF2A2419).copy(alpha = 0.60f),
    activeGlow = CathedralGold.copy(alpha = 0.50f)
)

val LocalCathedralColors = staticCompositionLocalOf { DarkCathedralColors }
