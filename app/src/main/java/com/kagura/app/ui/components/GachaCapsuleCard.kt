package com.kagura.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.Raridade
import com.kagura.app.ui.theme.*

@Composable
fun GachaCapsuleButton(rolling: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val t = rememberInfiniteTransition(label = "cap")
    val shake by t.animateFloat(-12f, 12f, infiniteRepeatable(tween(90), RepeatMode.Reverse), label = "shake")
    val glow by t.animateFloat(0.25f, 0.8f, infiniteRepeatable(tween(1400), RepeatMode.Reverse), label = "glow")
    Box(
        modifier.size(200.dp)
            .graphicsLayer { rotationZ = if (rolling) shake else 0f }
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = !rolling, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val c = center
            val r = size.minDimension * 0.34f
            drawCircle(
                Brush.radialGradient(listOf(KaguraViolet.copy(alpha = glow), Color.Transparent), c, size.minDimension / 2),
                size.minDimension / 2, c
            )
            val tl = Offset(c.x - r, c.y - r)
            val sz = Size(2 * r, 2 * r)
            drawArc(Brush.verticalGradient(listOf(KaguraPink, KaguraViolet), c.y - r, c.y), 180f, 180f, true, tl, sz)
            drawArc(Brush.verticalGradient(listOf(Color(0xFFE5E7EB), KaguraCyan), c.y, c.y + r), 0f, 180f, true, tl, sz)
            drawLine(Color.Black.copy(alpha = 0.6f), Offset(c.x - r, c.y), Offset(c.x + r, c.y), strokeWidth = 6f)
            drawCircle(Color.White, r * 0.22f, c)
            drawCircle(Color.Black.copy(alpha = 0.5f), r * 0.22f, c, style = Stroke(4f))
            drawCircle(Color.White.copy(alpha = 0.35f), r * 0.12f, Offset(c.x - r * 0.45f, c.y - r * 0.55f))
        }
    }
}

@Composable
fun GachaCapsuleCard(item: MediaItem, revealed: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val unlock by animateFloatAsState(if (revealed) 1f else 0f, tween(900), label = "unlock")
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier.width(240.dp).aspectRatio(2f / 3f)
            .tiltOnTouch()
            .rareBorder(item.rarity, shape)
            .clip(shape)
            .background(KaguraSurface)
            .clickable(enabled = revealed, onClick = onClick)
            .shimmer(revealed && item.rarity >= Raridade.RARO, item.rarity.color())
    ) {
        AsyncImage(
            model = item.coverUrl, contentDescription = null, contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            colorFilter = ColorFilter.tint(Color(0xFF050608)), modifier = Modifier.fillMaxSize()
        )
        Text(
            "???", Modifier.align(Alignment.Center).graphicsLayer { alpha = 1f - unlock },
            color = KaguraViolet, fontSize = 40.sp, fontWeight = FontWeight.Black
        )
        CoverImage(item.coverUrl, item.title, item.accent, Modifier.fillMaxSize().graphicsLayer { alpha = unlock })
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().graphicsLayer { alpha = unlock }
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, maxLines = 2)
            Pill(item.rarity.label.uppercase(), item.rarity.color())
        }
    }
}
