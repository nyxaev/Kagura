package com.kagura.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kagura.app.domain.model.Raridade

// Paleta alinhada ao splash: azul-noturno, lilas, rosa sakura e dourado suave
val KaguraBg = Color(0xFF120B2A)
val KaguraSurface = Color(0xFF1B1236)
val KaguraSurfaceHigh = Color(0xFF261A4A)
val KaguraViolet = Color(0xFFB86BFF)
val KaguraPink = Color(0xFFFF8FB1)
val KaguraCyan = Color(0xFF7FD6F5)
val KaguraText = Color(0xFFFFF4F8)
val KaguraMuted = Color(0xFFB9A8D9)
val KaguraGold = Color(0xFFFFD6A5)

// Translucent & Glassmorphic Palette
val KaguraGlassSurface = Color(0x991B1236)
val KaguraGlassHigh = Color(0xB3261A4A)
val KaguraGlassBorder = Color(0x33FFFFFF)
val KaguraGlassViolet = Color(0x33B86BFF)
val KaguraGlassPink = Color(0x33FF8FB1)
val KaguraGlassCyan = Color(0x337FD6F5)

// Gradientes do splash (uso opcional)
val KaguraBgBrush = Brush.verticalGradient(
    listOf(Color(0xFF1B1236), Color(0xFF120B2A), Color(0xFF07041A))
)
val KaguraMoonBrush = Brush.linearGradient(
    listOf(Color(0xFFFFD6A5), Color(0xFFFF8FB1), Color(0xFFB86BFF))
)

fun Raridade.color(): Color = when (this) {
    Raridade.COMUM -> Color(0xFF8B7FA8)
    Raridade.RARO -> KaguraCyan
    Raridade.EPICO -> KaguraViolet
    Raridade.LENDARIO -> KaguraPink
}