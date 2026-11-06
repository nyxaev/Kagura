package com.kagura.app.ui.screens.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.model.Raridade
import com.kagura.app.ui.components.*
import com.kagura.app.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(vm: DetailViewModel, onBack: () -> Unit) {
    val item by vm.item.collectAsStateWithLifecycle()
    val m = item ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = KaguraViolet) }
        return
    }
    val dominant by rememberDominantColor(m.coverUrl, Color(m.accent))
    val bg by animateColorAsState(dominant, tween(600), label = "bg")
    var rating by remember(m.userRating) { mutableFloatStateOf(m.userRating ?: 0f) }
    val shape = RoundedCornerShape(18.dp)

    Box(Modifier.fillMaxSize().background(KaguraBg)) {
        AsyncImage(
            model = m.coverUrl, contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(48.dp).alpha(0.6f)
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(bg.copy(alpha = 0.55f), KaguraBg.copy(alpha = 0.92f), KaguraBg))
            )
        )
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(Modifier.fillMaxWidth()) { TextButton(onBack) { Text("← Voltar", color = Color.White) } }

            CoverImage(
                m.coverUrl, m.title, m.accent,
                Modifier.fillMaxWidth(0.62f).aspectRatio(2f / 3f)
                    .tiltOnTouch().rareBorder(m.rarity, shape).clip(shape)
                    .shimmer(m.rarity >= Raridade.RARO, m.rarity.color())
            )
            Text(m.title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Pill(m.rarity.label, m.rarity.color())
                Pill(m.type.singular, KaguraCyan)
                if (m.year > 0) Pill(m.year.toString(), KaguraMuted)
                Pill(m.vibe, KaguraViolet)
                if (m.units > 1) Pill("${m.units} ${if (m.type == MediaType.MANGA) "cap." else "ep."}", KaguraMuted)
                Pill("★ ${m.score}", KaguraGold)
            }

            Text(m.synopsis, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = KaguraText)

            Text("Estado", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(ItemStatus.WANT, ItemStatus.IN_PROGRESS, ItemStatus.COMPLETED).forEach { st ->
                    FilterChip(
                        selected = m.status == st,
                        onClick = { vm.setStatus(if (m.status == st) ItemStatus.NONE else st) },
                        label = { Text(st.label(m.type)) }
                    )
                }
            }

            Column(Modifier.fillMaxWidth()) {
                Text("Sua nota: ${if (rating == 0f) "—" else "${rating.toInt()}/10"}", style = MaterialTheme.typography.titleMedium)
                Slider(
                    value = rating, onValueChange = { rating = it }, onValueChangeFinished = { vm.setRating(rating) },
                    valueRange = 0f..10f, steps = 9,
                    colors = SliderDefaults.colors(thumbColor = KaguraPink, activeTrackColor = KaguraPink)
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
