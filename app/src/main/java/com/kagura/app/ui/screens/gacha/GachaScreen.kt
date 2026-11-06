package com.kagura.app.ui.screens.gacha

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.usecase.GachaSource
import com.kagura.app.ui.components.CoverImage
import com.kagura.app.ui.components.GlassCard
import com.kagura.app.ui.components.Pill
import com.kagura.app.ui.components.label
import com.kagura.app.ui.components.rareBorder
import com.kagura.app.ui.components.tiltOnTouch
import com.kagura.app.ui.theme.*
import java.util.Locale

private data class ClimateOption(val name: String, val description: String, val icon: ImageVector)

private val climates = listOf(
    ClimateOption("Tranquilo", "Leve, divertido e relaxante", Icons.Outlined.Favorite),
    ClimateOption("Intenso", "Ação épica e adrenalina pura", Icons.Outlined.Star),
    ClimateOption("Super intenso", "Suspense profundo e reviravoltas", Icons.Outlined.Info)
)

@Composable
fun GachaScreen(vm: GachaViewModel, onOpen: (Int) -> Unit) {
    val s by vm.state.collectAsStateWithLifecycle()
    val result = s.result
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    val isAnime = s.mediaType == MediaType.ANIME

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Sorteador",
                style = MaterialTheme.typography.headlineLarge,
                color = KaguraText
            )
            Text(
                text = "Encontre a recomendação perfeita baseada no seu clima",
                style = MaterialTheme.typography.bodyMedium,
                color = KaguraMuted
            )
        }

        // Top Selector: Anime | Mangá com identidade visual própria e transição animada
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = KaguraGlassSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Anime button (Pink/Violet identity)
                val animeBg by animateColorAsState(
                    if (isAnime) KaguraGlassViolet else KaguraGlassSurface,
                    tween(300),
                    label = "anime_bg"
                )
                val animeBorder by animateColorAsState(
                    if (isAnime) KaguraViolet else KaguraGlassBorder,
                    tween(300),
                    label = "anime_border"
                )
                Surface(
                    onClick = { vm.setMediaType(MediaType.ANIME) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = animeBg,
                    border = BorderStroke(width = if (isAnime) 1.5.dp else 1.dp, color = animeBorder)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Anime",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isAnime) KaguraText else KaguraMuted,
                            fontWeight = if (isAnime) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // Manga button (Cyan/Gold identity)
                val mangaBg by animateColorAsState(
                    if (!isAnime) KaguraGlassCyan else KaguraGlassSurface,
                    tween(300),
                    label = "manga_bg"
                )
                val mangaBorder by animateColorAsState(
                    if (!isAnime) KaguraCyan else KaguraGlassBorder,
                    tween(300),
                    label = "manga_border"
                )
                Surface(
                    onClick = { vm.setMediaType(MediaType.MANGA) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = mangaBg,
                    border = BorderStroke(width = if (!isAnime) 1.5.dp else 1.dp, color = mangaBorder)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mangá",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (!isAnime) KaguraText else KaguraMuted,
                            fontWeight = if (!isAnime) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Filtro de Fonte: Catálogo Geral vs Quero Ver
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = KaguraGlassSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val isCatalog = s.source == GachaSource.CATALOG
                Surface(
                    onClick = { vm.setSource(GachaSource.CATALOG) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isCatalog) KaguraGlassViolet else KaguraGlassSurface,
                    border = BorderStroke(
                        width = if (isCatalog) 1.5.dp else 1.dp,
                        color = if (isCatalog) KaguraViolet else KaguraGlassBorder
                    )
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Catálogo Geral",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isCatalog) KaguraText else KaguraMuted
                        )
                    }
                }

                val isWant = s.source == GachaSource.WANT
                Surface(
                    onClick = { vm.setSource(GachaSource.WANT) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isWant) KaguraGlassViolet else KaguraGlassSurface,
                    border = BorderStroke(
                        width = if (isWant) 1.5.dp else 1.dp,
                        color = if (isWant) KaguraViolet else KaguraGlassBorder
                    )
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Minha Lista",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isWant) KaguraText else KaguraMuted
                        )
                    }
                }
            }
        }

        // Climate Selection Cards (Tranquilo, Intenso, Super intenso)
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Escolha o Clima",
                style = MaterialTheme.typography.labelSmall,
                color = KaguraMuted,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val allSelected = s.climate == null
                Surface(
                    onClick = { vm.setClimate(null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = if (allSelected) KaguraGlassViolet else KaguraGlassSurface,
                    border = BorderStroke(
                        width = if (allSelected) 1.5.dp else 1.dp,
                        color = if (allSelected) KaguraViolet else KaguraGlassBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (allSelected) KaguraPink else KaguraMuted,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Qualquer Clima",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (allSelected) KaguraText else KaguraMuted
                            )
                            Text(
                                text = "Sorteio livre entre todos os títulos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = KaguraMuted
                            )
                        }
                    }
                }

                climates.forEach { climate ->
                    val selected = s.climate == climate.name
                    Surface(
                        onClick = { vm.setClimate(if (selected) null else climate.name) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) KaguraGlassViolet else KaguraGlassSurface,
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) KaguraViolet else KaguraGlassBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = climate.icon,
                                contentDescription = null,
                                tint = if (selected) KaguraPink else KaguraMuted,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = climate.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (selected) KaguraText else KaguraMuted
                                )
                                Text(
                                    text = climate.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = KaguraMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Botão Principal de Sorteio
        Button(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                vm.roll()
            },
            enabled = s.phase != GachaPhase.ROLLING,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = KaguraPink,
                disabledContainerColor = KaguraPink.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (s.phase == GachaPhase.ROLLING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = KaguraText,
                        strokeWidth = 2.dp
                    )
                    Text("Sorteando...", style = MaterialTheme.typography.titleLarge)
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = KaguraText
                    )
                    Text("Sortear Recomendação", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        // Alerta quando não houver mídias compatíveis
        if (s.empty) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = KaguraGlassPink,
                borderColor = KaguraGold.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = KaguraGold
                    )
                    Text(
                        text = if (s.source == GachaSource.WANT)
                            "Nenhum título encontrado na sua lista com estes filtros."
                        else "Nenhum título encontrado com estes filtros.",
                        color = KaguraGold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Card de Resultado do Sorteio
        Crossfade(targetState = s.phase to result, label = "gacha_result") { (phase, item) ->
            if (phase == GachaPhase.ROLLING) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = KaguraGlassSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = KaguraPink,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "Buscando recomendação ideal...",
                            style = MaterialTheme.typography.titleMedium,
                            color = KaguraText
                        )
                    }
                }
            } else if (phase == GachaPhase.REVEAL && item != null) {
                DrawnResultCard(
                    item = item,
                    revealed = s.revealed,
                    onOpen = { onOpen(item.id) },
                    onRollAgain = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        vm.roll()
                    },
                    onAddWant = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        vm.addWant()
                    },
                    onShare = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Recomendação Kagura: ${item.title}!")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, null))
                    }
                )
            } else if (phase == GachaPhase.IDLE && !s.empty && result == null) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = KaguraGlassSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = KaguraViolet,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Pronto para descobrir algo novo?",
                            style = MaterialTheme.typography.titleLarge,
                            color = KaguraText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Escolha o tipo e o clima acima e toque em 'Sortear Recomendação'.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KaguraMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawnResultCard(
    item: MediaItem,
    revealed: Boolean,
    onOpen: () -> Unit,
    onRollAgain: () -> Unit,
    onAddWant: () -> Unit,
    onShare: () -> Unit
) {
    val alphaAnim by animateFloatAsState(if (revealed) 1f else 0f, tween(600), label = "alpha")
    val shape = RoundedCornerShape(20.dp)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .tiltOnTouch()
            .graphicsLayer { alpha = alphaAnim },
        shape = shape,
        backgroundColor = KaguraGlassSurface,
        borderColor = item.rarity.color().copy(alpha = 0.5f),
        elevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CoverImage(
                    url = item.coverUrl,
                    title = item.title,
                    accent = item.accent,
                    modifier = Modifier
                        .width(100.dp)
                        .aspectRatio(2f / 3f)
                        .rareBorder(item.rarity, shape)
                        .clip(shape)
                        .clickable(onClick = onOpen)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Pill(item.rarity.label.uppercase(), item.rarity.color())
                        if (item.score > 0f) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = KaguraGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f", item.score),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = KaguraGold
                                )
                            }
                        }
                    }

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = KaguraText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${item.type.singular} • ${item.year} • ${item.vibe}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = KaguraMuted
                    )

                    Text(
                        text = item.synopsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = KaguraText.copy(alpha = 0.85f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, KaguraGlassBorder)
                ) {
                    Text("Ver Detalhes", style = MaterialTheme.typography.bodyMedium, color = KaguraText)
                }

                if (item.status != ItemStatus.WANT) {
                    Button(
                        onClick = onAddWant,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KaguraViolet)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Quero Ver", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Salvo", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                IconButton(
                    onClick = onRollAgain,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Sortear Outro",
                        tint = KaguraPink
                    )
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Compartilhar",
                        tint = KaguraText
                    )
                }
            }
        }
    }
}
