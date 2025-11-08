package com.nerver.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.nerver.data.AppTheme

private val PrimaryColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = PrimaryText,
    secondary = PrimaryLightBlue,
    onSecondary = PrimaryText,
    tertiary = PrimaryDarkBlue,
    onTertiary = PrimaryText,
    background = PrimarySurface,
    onBackground = PrimaryText,
    surface = PrimaryDarkBlue,
    onSurface = PrimaryText
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkText,
    secondary = DarkPrimary,
    onSecondary = DarkText,
    tertiary = DarkPrimary,
    onTertiary = DarkText,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightText,
    secondary = LightPrimary,
    onSecondary = LightText,
    tertiary = LightPrimary,
    onTertiary = LightText,
    background = LightBackground,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText
)

@Composable
fun NerverTheme(
    appTheme: AppTheme = AppTheme.PRIMARY,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.PRIMARY -> PrimaryColorScheme
        AppTheme.DARK -> DarkColorScheme
        AppTheme.LIGHT -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}