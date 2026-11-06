package com.kagura.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kagura.app.ui.theme.KaguraGlassBorder
import com.kagura.app.ui.theme.KaguraGlassSurface
import com.kagura.app.ui.theme.KaguraSurface

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = KaguraGlassSurface,
    borderColor: Color = KaguraGlassBorder,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(borderWidth, borderColor),
        shadowElevation = elevation,
        content = content
    )
}

fun Modifier.glassmorphic(
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = KaguraGlassSurface,
    borderColor: Color = KaguraGlassBorder
): Modifier = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            listOf(
                backgroundColor,
                KaguraSurface.copy(alpha = 0.85f)
            )
        )
    )
    .border(1.dp, borderColor, shape)
