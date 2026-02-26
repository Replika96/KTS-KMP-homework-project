package org.kts.tazmin.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography


@Composable
fun CatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkThemeColors else LightThemeColors

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            primaryContainer = colors.primaryContainer,
            onPrimaryContainer = colors.onPrimaryContainer,

            secondary = colors.secondary,
            onSecondary = colors.onSecondary,
            secondaryContainer = colors.secondaryContainer,
            onSecondaryContainer = colors.onSecondaryContainer,

            tertiary = colors.tertiary,
            onTertiary = colors.onTertiary,
            tertiaryContainer = colors.tertiaryContainer,
            onTertiaryContainer = colors.onTertiaryContainer,

            background = colors.background,
            onBackground = colors.onBackground,

            surface = colors.surface,
            onSurface = colors.onSurface,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.onSurfaceVariant,

            error = colors.error,
            onError = colors.onError,
            errorContainer = colors.errorContainer,
            onErrorContainer = colors.onErrorContainer,

            outline = colors.outline,
            outlineVariant = colors.outlineVariant,
            inverseSurface = colors.inverseSurface,
            inverseOnSurface = colors.inverseOnSurface,
            inversePrimary = colors.inversePrimary,

            scrim = colors.scrim
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            primaryContainer = colors.primaryContainer,
            onPrimaryContainer = colors.onPrimaryContainer,

            secondary = colors.secondary,
            onSecondary = colors.onSecondary,
            secondaryContainer = colors.secondaryContainer,
            onSecondaryContainer = colors.onSecondaryContainer,

            tertiary = colors.tertiary,
            onTertiary = colors.onTertiary,
            tertiaryContainer = colors.tertiaryContainer,
            onTertiaryContainer = colors.onTertiaryContainer,

            background = colors.background,
            onBackground = colors.onBackground,

            surface = colors.surface,
            onSurface = colors.onSurface,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.onSurfaceVariant,

            error = colors.error,
            onError = colors.onError,
            errorContainer = colors.errorContainer,
            onErrorContainer = colors.onErrorContainer,

            outline = colors.outline,
            outlineVariant = colors.outlineVariant,
            inverseSurface = colors.inverseSurface,
            inverseOnSurface = colors.inverseOnSurface,
            inversePrimary = colors.inversePrimary,

            scrim = colors.scrim
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}