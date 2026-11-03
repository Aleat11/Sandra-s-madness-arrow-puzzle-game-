package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BriocheDarkPrimary,
    onPrimary = BriocheDarkOnPrimary,
    primaryContainer = BriocheDarkPrimaryContainer,
    onPrimaryContainer = BriocheDarkOnPrimaryContainer,
    secondary = LettuceDarkSecondary,
    onSecondary = LettuceDarkOnSecondary,
    secondaryContainer = LettuceDarkSecondaryContainer,
    onSecondaryContainer = LettuceDarkOnSecondaryContainer,
    tertiary = BerryShakeDarkTertiary,
    onTertiary = BerryShakeDarkOnTertiary,
    tertiaryContainer = BerryShakeDarkTertiaryContainer,
    onTertiaryContainer = BerryShakeDarkOnTertiaryContainer,
    background = CrochetDarkBg,
    onBackground = CreamDarkText,
    surface = CrochetDarkSurface,
    onSurface = CreamDarkText,
    surfaceVariant = CrochetDarkSurfaceVariant,
    onSurfaceVariant = SoftTanDarkSubtext,
    error = Color(0xFFFFB4AB)
)

private val LightColorScheme = lightColorScheme(
    primary = BriochePrimary,
    onPrimary = BriocheOnPrimary,
    primaryContainer = BriochePrimaryContainer,
    onPrimaryContainer = BriocheOnPrimaryContainer,
    secondary = LettuceSecondary,
    onSecondary = LettuceOnSecondary,
    secondaryContainer = LettuceSecondaryContainer,
    onSecondaryContainer = LettuceOnSecondaryContainer,
    tertiary = BerryShakeTertiary,
    onTertiary = BerryShakeOnTertiary,
    tertiaryContainer = BerryShakeTertiaryContainer,
    onTertiaryContainer = BerryShakeOnTertiaryContainer,
    background = CrochetLinenLight,
    onBackground = EspressoText,
    surface = CrochetCardLight,
    onSurface = EspressoText,
    surfaceVariant = CrochetSurfaceVariantLight,
    onSurfaceVariant = WarmTaupeSubtext,
    error = Color(0xFFBA1A1A)
)

@Composable
fun MyApplicationTheme(
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
