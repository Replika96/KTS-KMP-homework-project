package org.kts.tazmin.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF6B7FA3)       // спокойный сине-серый
val PrimaryContainer = Color(0xFFDDE3F1)

val Secondary = Color(0xFF8C8C8C)     // нейтральный серый
val SecondaryContainer = Color(0xFFE5E5E5)

val Tertiary = Color(0xFFA67C6B)      // мягкий теплый акцент
val TertiaryContainer = Color(0xFFF1E4DE)

val Background = Color(0xFFF7F7F7)    // почти белый
val Surface = Color(0xFFFFFFFF)
val SurfaceVariant = Color(0xFFEDEDED)

val TextPrimary = Color(0xFF1C1C1C)
val TextSecondary = Color(0xFF4F4F4F)
val Outline = Color(0xFFD0D0D0)
val LightThemeColors = AppColors(

    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = TextPrimary,

    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = TextPrimary,

    tertiary = Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = TextPrimary,

    background = Background,
    onBackground = TextPrimary,

    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    outline = Outline,
    outlineVariant = SurfaceVariant,

    inverseSurface = TextPrimary,
    inverseOnSurface = Color.White,
    inversePrimary = Primary,

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
