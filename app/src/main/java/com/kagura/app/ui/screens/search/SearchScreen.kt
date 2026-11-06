package com.kagura.app.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.ui.components.CoverImage
import com.kagura.app.ui.components.GlassCard
import com.kagura.app.ui.components.Pill
import com.kagura.app.ui.components.label
import com.kagura.app.ui.components.rareBorder
import com.kagura.app.ui.components.shimmer
import com.kagura.app.ui.components.tiltOnTouch
import com.kagura.app.ui.theme.*
import java.util.Locale

private val popularSuggestions = listOf("Attack on Titan", "One Piece", "Naruto", "Jujutsu Kaisen", "Death Note", "Frieren")

@Composable
fun SearchScreen(vm: SearchViewModel, onOpen: (Int) -> Unit) {
    val q by vm.query.collectAsStateWithLifecycle()
    val state by vm.uiState.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Descobrir Anime",
            style = MaterialTheme.typography.headlineLarge,
            color = KaguraText
        )
        Text(
            text = "Encontre animes instantaneamente",
            style = MaterialTheme.typography.bodyMedium,
            color = KaguraMuted,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        OutlinedTextField(
            value = q,
            onValueChange = vm::onQuery,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Buscar anime por título...",
                    color = KaguraMuted.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = KaguraViolet
                )
            },
            trailingIcon = {
                AnimatedVisibility(visible = q.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    IconButton(onClick = { vm.onQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpar busca",
                            tint = KaguraPink
                        )
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = KaguraGlassHigh,
                unfocusedContainerColor = KaguraGlassSurface,
                focusedBorderColor = KaguraViolet,
                unfocusedBorderColor = KaguraGlassBorder,
                cursorColor = KaguraPink,
                focusedTextColor = KaguraText,
                unfocusedTextColor = KaguraText
            )
        )

        Spacer(Modifier.height(14.dp))

        Crossfade(targetState = state, label = "search_state") { s ->
            when (s) {
                is SearchUiState.Idle -> IdleSearchContent(onSelectSuggestion = vm::onQuery)
                is SearchUiState.Loading -> LoadingSearchContent()
                is SearchUiState.Success -> SuccessSearchContent(s, onOpen)
                is SearchUiState.Empty -> EmptySearchContent()
                is SearchUiState.Error -> ErrorSearchContent(s.message)
            }
        }
    }
}

@Composable
private fun IdleSearchContent(onSelectSuggestion: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = KaguraViolet,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Digite ao menos 2 caracteres para buscar animes",
            style = MaterialTheme.typography.titleMedium,
            color = KaguraText,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Acesse milhares de animes online ou no seu banco local",
            style = MaterialTheme.typography.bodyMedium,
            color = KaguraMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Sugestões em Alta",
            style = MaterialTheme.typography.labelSmall,
            color = KaguraViolet,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            popularSuggestions.chunked(2).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { sug ->
                        GlassCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectSuggestion(sug) },
                            backgroundColor = KaguraGlassSurface,
                            elevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = null,
                                    tint = KaguraMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = sug,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = KaguraText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    if (row.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingSearchContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(6) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = KaguraGlassSurface
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .width(60.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(KaguraSurface)
                            .shimmer()
                    )
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            Modifier
                                .fillMaxWidth(0.7f)
                                .height(18.dp)
                                .background(KaguraSurface)
                                .shimmer()
                        )
                        Box(
                            Modifier
                                .fillMaxWidth(0.4f)
                                .height(14.dp)
                                .background(KaguraSurface)
                                .shimmer()
                        )
                        Box(
                            Modifier
                                .width(80.dp)
                                .height(20.dp)
                                .clip(CircleShape)
                                .background(KaguraSurface)
                                .shimmer()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessSearchContent(
    s: SearchUiState.Success,
    onOpen: (Int) -> Unit
) {
    val filtered = s.items

    Column(Modifier.fillMaxSize()) {
        if (s.isOffline) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                backgroundColor = KaguraGlassPink,
                borderColor = KaguraGold.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = KaguraGold
                    )
                    Text(
                        text = "Modo Offline: Exibindo resultados do catálogo local",
                        color = KaguraGold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            EmptySearchContent()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered, key = { it.id }) { item ->
                    SearchRow(item) { onOpen(item.id) }
                }
            }
        }
    }
}

@Composable
private fun SearchRow(m: MediaItem, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .tiltOnTouch()
            .clickable(onClick = onClick),
        shape = shape,
        backgroundColor = KaguraGlassSurface,
        borderColor = KaguraGlassBorder,
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoverImage(
                url = m.coverUrl,
                title = m.title,
                accent = m.accent,
                modifier = Modifier
                    .width(60.dp)
                    .aspectRatio(2f / 3f)
                    .rareBorder(m.rarity, shape)
                    .clip(shape)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = m.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = KaguraText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val yearStr = if (m.year > 0) " • ${m.year}" else ""
                Text(
                    text = "${m.type.singular}$yearStr • ${m.vibe}",
                    color = KaguraMuted,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(2.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Pill(m.rarity.label, m.rarity.color())
                    if (m.status != ItemStatus.NONE) {
                        Pill(m.status.label(m.type), KaguraCyan)
                    }
                    val displayRating = m.userRating ?: if (m.score > 0f) m.score else null
                    if (displayRating != null && displayRating > 0f) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = KaguraGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f", displayRating),
                                style = MaterialTheme.typography.labelSmall,
                                color = KaguraGold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = KaguraMuted,
                modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Nenhum anime encontrado",
                style = MaterialTheme.typography.titleLarge,
                color = KaguraText,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tente buscar com outro nome de anime",
                style = MaterialTheme.typography.bodyMedium,
                color = KaguraMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorSearchContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = KaguraPink,
                modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Ops! Algo deu errado",
                style = MaterialTheme.typography.titleLarge,
                color = KaguraPink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = KaguraMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
