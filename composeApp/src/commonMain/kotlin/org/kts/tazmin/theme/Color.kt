package org.kts.tazmin.theme

import androidx.compose.ui.graphics.Color

// основные цвета RedCat
val GingerCat = Color(0xFFFF8A5C)    // основной
//val KittenPink = Color(0xFFFFB7B7)
val CreamPaws = Color(0xFFFFE9D6)
val SoftWhiskers = Color(0xFFF5E6D3)
val CatNose = Color(0xFFFF9E9E)
val CozyBrown = Color(0xFF8B5E3C)
val NightPaws = Color(0xFFC6623B)

val LightThemeColors = AppColors(
    primary = GingerCat,
    onPrimary = Color.White,
    primaryContainer = CreamPaws,
    onPrimaryContainer = CozyBrown,

    secondary = CatNose,
    onSecondary = CozyBrown,
    secondaryContainer = CatNose.copy(alpha = 0.2f),
    onSecondaryContainer = CozyBrown,

    tertiary = CozyBrown,
    onTertiary = CreamPaws,
    tertiaryContainer = CozyBrown.copy(alpha = 0.1f),
    onTertiaryContainer = CozyBrown,

    background = CreamPaws,
    onBackground = CozyBrown,

    surface = Color.White,
    onSurface = CozyBrown,
    surfaceVariant = SoftWhiskers,
    onSurfaceVariant = CozyBrown.copy(alpha = 0.8f),

    error = Color(0xFFE57373),
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),

    outline = GingerCat.copy(alpha = 0.3f),
    outlineVariant = SoftWhiskers,

    inverseSurface = CozyBrown,
    inverseOnSurface = CreamPaws,
    inversePrimary = CatNose,

    shadow = Color.Black.copy(alpha = 0.1f),
    scrim = Color.Black.copy(alpha = 0.3f)
)

/*val DarkThemeColors = AppColors(
    primary = CatNose,
    onPrimary = CozyBrown,
    primaryContainer = GingerCat.copy(alpha = 0.2f),
    onPrimaryContainer = CreamPaws,

    secondary = GingerCat,
    onSecondary = CozyBrown,
    secondaryContainer = GingerCat.copy(alpha = 0.15f),
    onSecondaryContainer = GingerCat,

    tertiary = CreamPaws,
    onTertiary = CozyBrown,
    tertiaryContainer = CreamPaws.copy(alpha = 0.15f),
    onTertiaryContainer = CreamPaws,

    background = NightPaws,
    onBackground = CreamPaws,

    surface = CozyBrown,
    onSurface = CreamPaws,
    surfaceVariant = CozyBrown.copy(alpha = 0.6f),
    onSurfaceVariant = CreamPaws.copy(alpha = 0.8f),

    error = Color(0xFFEF9A9A),
    onError = NightPaws,
    errorContainer = Color(0xFF5F2A2A),
    onErrorContainer = Color(0xFFFFCDD2),

    outline = CatNose.copy(alpha = 0.3f),
    outlineVariant = CozyBrown.copy(alpha = 0.5f),

    inverseSurface = CreamPaws,
    inverseOnSurface = CozyBrown,
    inversePrimary = GingerCat,

    shadow = Color.Black.copy(alpha = 0.4f),
    scrim = Color.Black.copy(alpha = 0.6f)
)*/

data class AppColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,

    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,

    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color = tertiary.copy(alpha = 0.2f),
    val onTertiaryContainer: Color = tertiary,

    val background: Color,
    val onBackground: Color,

    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,

    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,

    val outline: Color,
    val outlineVariant: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,

    val shadow: Color,
    val scrim: Color
)
