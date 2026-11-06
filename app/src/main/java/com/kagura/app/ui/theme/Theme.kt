package com.kagura.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeonScheme = darkColorScheme(
    primary = KaguraViolet, onPrimary = KaguraBg,
    secondary = KaguraPink, onSecondary = KaguraBg,
    tertiary = KaguraCyan, onTertiary = KaguraBg,
    background = KaguraBg, onBackground = KaguraText,
    surface = KaguraSurface, onSurface = KaguraText,
    surfaceVariant = KaguraSurfaceHigh, onSurfaceVariant = KaguraMuted,
    secondaryContainer = KaguraViolet.copy(alpha = 0.30f), onSecondaryContainer = KaguraText,
    outline = KaguraGlassBorder,
    outlineVariant = Color(0x1AFFFFFF)
)

@Composable
fun KaguraTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = NeonScheme, typography = KaguraTypography, content = content)
}