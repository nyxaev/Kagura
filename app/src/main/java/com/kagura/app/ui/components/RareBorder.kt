package com.kagura.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kagura.app.domain.model.Raridade
import com.kagura.app.ui.theme.KaguraCyan
import com.kagura.app.ui.theme.KaguraGold
import com.kagura.app.ui.theme.color

@Composable
fun Modifier.rareBorder(rarity: Raridade, shape: Shape = RoundedCornerShape(14.dp)): Modifier {
    val base = rarity.color()
    val width = when (rarity) { Raridade.COMUM -> 1.dp; Raridade.RARO -> 2.dp; Raridade.EPICO -> 2.5.dp; Raridade.LENDARIO -> 3.dp }
    val elevation = when (rarity) { Raridade.COMUM -> 2.dp; Raridade.RARO -> 6.dp; Raridade.EPICO -> 8.dp; Raridade.LENDARIO -> 14.dp }

    val shaded = this.shadow(elevation, shape, ambientColor = base, spotColor = base)

    return when (rarity) {
        Raridade.COMUM, Raridade.RARO -> {
            val brush = SolidColor(base)
            shaded.border(width, brush, shape)
        }
        Raridade.EPICO, Raridade.LENDARIO -> {
            val t = rememberInfiniteTransition(label = "rb")
            val shiftAnim = t.animateFloat(0f, 400f, infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Restart), label = "s")
            shaded.drawWithContent {
                drawContent()
                val shift = shiftAnim.value
                val brush: Brush = when (rarity) {
                    Raridade.LENDARIO -> Brush.linearGradient(listOf(base, KaguraGold, KaguraCyan, base), Offset(shift, shift), Offset(shift + 200f, shift + 200f), TileMode.Repeated)
                    else -> Brush.linearGradient(listOf(base, KaguraCyan, base), Offset(shift, shift), Offset(shift + 200f, shift + 200f), TileMode.Repeated)
                }
                val outline = shape.createOutline(size, layoutDirection, this)
                val style = Stroke(width.toPx())
                when (outline) {
                    is Outline.Rectangle -> drawRect(brush, outline.rect.topLeft, outline.rect.size, style = style)
                    is Outline.Rounded -> drawPath(Path().apply { addRoundRect(outline.roundRect) }, brush = brush, style = style)
                    is Outline.Generic -> drawPath(outline.path, brush = brush, style = style)
                }
            }
        }
    }
}
