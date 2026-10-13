package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NumLime,
    onPrimary = Color(0xFF090B0A),
    primaryContainer = NumSurfaceElevated,
    onPrimaryContainer = NumLime,
    secondary = NumLime,
    onSecondary = Color(0xFF090B0A),
    background = NumBackground,
    onBackground = NumTextPrimary,
    surface = NumSurface,
    onSurface = NumTextPrimary,
    surfaceVariant = NumSurfaceElevated,
    onSurfaceVariant = NumTextSecondary,
    outline = NumBorder,
    outlineVariant = NumBorderActive,
    error = NumError,
    onError = Color(0xFF090B0A),
    errorContainer = NumErrorBg,
    onErrorContainer = NumError
)

private val LightColorScheme = lightColorScheme(
    primary = NumLightLime,
    onPrimary = Color.White,
    primaryContainer = NumLightSurfaceElevated,
    onPrimaryContainer = NumLightLime,
    secondary = NumLightLime,
    onSecondary = Color.White,
    background = NumLightBackground,
    onBackground = NumLightTextPrimary,
    surface = NumLightSurface,
    onSurface = NumLightTextPrimary,
    surfaceVariant = NumLightSurfaceElevated,
    onSurfaceVariant = NumLightTextSecondary,
    outline = NumLightBorder,
    outlineVariant = NumLightBorderActive,
    error = NumError,
    onError = Color.White,
    errorContainer = NumErrorBg,
    onErrorContainer = NumError
)

@Composable
fun NumK2ITheme(
    darkTheme: Boolean = true, // Default to dark workstation theme as requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
