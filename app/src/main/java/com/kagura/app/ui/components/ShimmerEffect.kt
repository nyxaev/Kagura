package com.kagura.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun Modifier.shimmer(enabled: Boolean = true, color: Color = Color.White, durationMs: Int = 2000): Modifier {
    if (!enabled) return this
    val t = rememberInfiniteTransition(label = "shimmer")
    val pAnim = t.animateFloat(-0.5f, 1.5f, infiniteRepeatable(tween(durationMs, easing = LinearEasing)), label = "p")
    return drawWithContent {
        drawContent()
        val w = size.width
        val x = pAnim.value * w
        drawRect(
            Brush.linearGradient(
                listOf(Color.Transparent, color.copy(alpha = 0.35f), Color.Transparent),
                start = Offset(x - w * 0.3f, 0f),
                end = Offset(x + w * 0.3f, size.height * 0.5f)
            )
        )
    }
}
