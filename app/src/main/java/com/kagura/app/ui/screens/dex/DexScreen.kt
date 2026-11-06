package com.kagura.app.ui.screens.dex

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.model.Raridade
import com.kagura.app.ui.components.CoverImage
import com.kagura.app.ui.components.GlassCard
import com.kagura.app.ui.components.rareBorder
import com.kagura.app.ui.components.shimmer
import com.kagura.app.ui.components.tiltOnTouch
import com.kagura.app.ui.theme.*
import kotlin.math.roundToInt

private val tabs = listOf(MediaType.MOVIE, MediaType.ANIME, MediaType.MANGA)

@Composable
fun DexScreen(vm: DexViewModel, onOpen: (Int) -> Unit) {
    val s by vm.state.collectAsStateWithLifecycle()
    val prog by animateFloatAsState(s.progress, label = "prog")
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Coleção & Dex",
            style = MaterialTheme.typography.headlineLarge,
            color = KaguraText
        )
        Text(
            text = "Gerencie seus filmes, animes e mangás coletados",
            style = MaterialTheme.typography.bodyMedium,
            color = KaguraMuted,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        TabRow(
            selectedTabIndex = tabs.indexOf(s.type),
            containerColor = Color.Transparent,
            contentColor = KaguraViolet
        ) {
            tabs.forEach { t ->
                Tab(
                    selected = s.type == t,
                    onClick = { vm.setType(t) },
                    text = {
                        Text(
                            text = t.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (s.type == t) KaguraText else KaguraMuted
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = KaguraGlassSurface
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Progresso da Coleção", style = MaterialTheme.typography.titleMedium, color = KaguraText)
                    Text("${(s.progress * 100).roundToInt()}%", style = MaterialTheme.typography.titleLarge, color = KaguraPink)
                }
                LinearProgressIndicator(
                    progress = { prog },
                    color = KaguraPink,
                    trackColor = KaguraSurfaceHigh,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Text("${s.done} de ${s.total} itens concluídos", color = KaguraMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            item {
                val selected = s.rarity == null
                Surface(
                    onClick = { vm.setRarity(null) },
                    shape = RoundedCornerShape(50),
                    color = if (selected) KaguraGlassViolet else KaguraGlassSurface,
                    border = BorderStroke(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = if (selected) KaguraViolet else KaguraGlassBorder
                    )
                ) {
                    Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Text("Todas", style = MaterialTheme.typography.titleMedium, color = if (selected) KaguraText else KaguraMuted)
                    }
                }
            }
            items(Raridade.entries.toList()) { r ->
                val selected = s.rarity == r
                Surface(
                    onClick = { vm.setRarity(if (s.rarity == r) null else r) },
                    shape = RoundedCornerShape(50),
                    color = if (selected) KaguraGlassViolet else KaguraGlassSurface,
                    border = BorderStroke(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = if (selected) KaguraViolet else KaguraGlassBorder
                    )
                ) {
                    Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Text(r.label, style = MaterialTheme.typography.titleMedium, color = if (selected) KaguraText else KaguraMuted)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(110.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(s.items, key = { it.id }) { DexCard(it) { onOpen(it.id) } }
        }
    }
}

@Composable
private fun DexCard(item: MediaItem, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .tiltOnTouch(6f)
            .rareBorder(item.rarity, shape)
            .clip(shape)
            .clickable(onClick = onClick)
            .shimmer(item.rarity >= Raridade.RARO, item.rarity.color())
    ) {
        CoverImage(item.coverUrl, item.title, item.accent, Modifier.fillMaxSize().alpha(if (item.status == ItemStatus.NONE) 0.8f else 1f))
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))))
                .padding(6.dp)
        ) {
            Text(item.title, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall, color = Color.White)
        }
        if (item.status == ItemStatus.COMPLETED) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .background(KaguraPink, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
