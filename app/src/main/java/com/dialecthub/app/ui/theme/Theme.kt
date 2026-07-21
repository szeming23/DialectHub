package com.dialecthub.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.dialecthub.app.data.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = Vermillion40,
    onPrimary = Neutral99,
    primaryContainer = Vermillion80,
    onPrimaryContainer = Vermillion20,
    secondary = Gold40,
    onSecondary = Neutral99,
    secondaryContainer = Gold80,
    onSecondaryContainer = Gold20,
    tertiary = Jade40,
    onTertiary = Neutral99,
    tertiaryContainer = Jade80,
    onTertiaryContainer = Jade20,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = Neutral95,
    onSurfaceVariant = Neutral20,
    error = Error40,
    onError = Neutral99
)

private val DarkColors = darkColorScheme(
    primary = Vermillion80,
    onPrimary = Vermillion20,
    primaryContainer = Vermillion40,
    onPrimaryContainer = Vermillion80,
    secondary = Gold80,
    onSecondary = Gold20,
    secondaryContainer = Gold40,
    onSecondaryContainer = Gold80,
    tertiary = Jade80,
    onTertiary = Jade20,
    tertiaryContainer = Jade40,
    onTertiaryContainer = Jade80,
    background = Neutral10,
    onBackground = Neutral95,
    surface = Neutral10,
    onSurface = Neutral95,
    surfaceVariant = Neutral20,
    onSurfaceVariant = Neutral95,
    error = Error80,
    onError = Vermillion20
)

@Composable
fun DialectHubTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DialectHubTypography,
        content = content
    )
}
