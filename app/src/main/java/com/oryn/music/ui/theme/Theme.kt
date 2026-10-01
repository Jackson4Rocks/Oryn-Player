package com.oryn.music.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OrynColors = darkColorScheme(
    primary = Color(0xFFE5D4FF),
    onPrimary = Color(0xFF271536),
    background = Color(0xFF070709),
    onBackground = Color(0xFFF7F3FA),
    surface = Color(0xFF0D0C10),
    onSurface = Color(0xFFF7F3FA),
    surfaceVariant = Color(0xFF17151B),
    outline = Color(0xFF39343F)
)

@Composable
fun OrynTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = OrynColors, content = content)
}
