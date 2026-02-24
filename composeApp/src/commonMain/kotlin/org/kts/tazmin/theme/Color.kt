package org.kts.tazmin.theme


import androidx.compose.ui.graphics.Color


val WarmCream = Color(0xFFFFF6E5)
val SoftMocha = Color(0xFF6D5C4D)
val WarmBrown = Color(0xFF4E4037)
val Peach = Color(0xFFFFB7A5)
val SoftSand = Color(0xFFE8DCC8)

val LightThemeColors = AppColors(
    primary = SoftMocha,
    onPrimary = WarmCream,
    primaryContainer = SoftSand,
    onPrimaryContainer = WarmBrown,

    secondary = Peach,
    onSecondary = WarmBrown,
    secondaryContainer = Peach.copy(alpha = 0.25f),
    onSecondaryContainer = WarmBrown,

    tertiary = WarmBrown,
    onTertiary = WarmCream,

    background = WarmCream,
    onBackground = WarmBrown,

    surface = Color.White,
    onSurface = WarmBrown,
    surfaceVariant = SoftSand,
    onSurfaceVariant = SoftMocha,

    error = Color(0xFFE57373),
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),

    outline = SoftMocha.copy(alpha = 0.4f),
    outlineVariant = SoftSand,

    inverseSurface = WarmBrown,
    inverseOnSurface = WarmCream,
    inversePrimary = Peach,

    shadow = Color.Black.copy(alpha = 0.15f),
    scrim = Color.Black.copy(alpha = 0.3f)
)

val DarkThemeColors = AppColors(
    primary = Peach,
    onPrimary = WarmBrown,
    primaryContainer = SoftMocha,
    onPrimaryContainer = WarmCream,

    secondary = SoftSand,
    onSecondary = WarmBrown,
    secondaryContainer = SoftSand.copy(alpha = 0.15f),
    onSecondaryContainer = SoftSand,

    tertiary = Peach,
    onTertiary = WarmBrown,

    background = WarmBrown,
    onBackground = WarmCream,

    surface = SoftMocha,
    onSurface = WarmCream,
    surfaceVariant = SoftMocha.copy(alpha = 0.8f),
    onSurfaceVariant = WarmCream.copy(alpha = 0.8f),

    error = Color(0xFFEF9A9A),
    onError = WarmBrown,
    errorContainer = Color(0xFF5F2A2A),
    onErrorContainer = Color(0xFFFFCDD2),

    outline = SoftSand.copy(alpha = 0.4f),
    outlineVariant = SoftMocha,

    inverseSurface = WarmCream,
    inverseOnSurface = WarmBrown,
    inversePrimary = SoftMocha,

    shadow = Color.Black.copy(alpha = 0.4f),
    scrim = Color.Black.copy(alpha = 0.5f)
)


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