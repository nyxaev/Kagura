package com.kagura.app.ui.screens.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kagura.app.ui.components.GlassCard
import com.kagura.app.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(vm: ProfileViewModel) {
    val s by vm.stats.collectAsStateWithLifecycle()
    val prog by animateFloatAsState(s.progress, label = "p")
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text("Perfil & Estatísticas", style = MaterialTheme.typography.headlineLarge, color = KaguraText)
            Text("Acompanhe seu progresso e estatísticas gerais", style = MaterialTheme.typography.bodyMedium, color = KaguraMuted)
        }

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = KaguraGlassSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { prog },
                        modifier = Modifier.size(130.dp),
                        strokeWidth = 10.dp,
                        color = KaguraPink,
                        trackColor = KaguraSurfaceHigh
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(s.progress * 100).roundToInt()}%", style = MaterialTheme.typography.headlineMedium)
                        Text("do Dex", color = KaguraMuted, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Text("${s.completed} de ${s.total} itens concluídos", color = KaguraText, style = MaterialTheme.typography.titleMedium)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Episódios salvos", s.savedEpisodes.toString(), KaguraCyan, Modifier.weight(1f))
            StatCard("Capítulos salvos", s.savedChapters.toString(), KaguraViolet, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Quero ver", s.want.toString(), KaguraPink, Modifier.weight(1f))
            StatCard("Em andamento", s.inProgress.toString(), KaguraCyan, Modifier.weight(1f))
            StatCard("Lendários", s.legendaryDone.toString(), KaguraGold, Modifier.weight(1f))
        }

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = KaguraGlassSurface
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Progresso por Categoria", style = MaterialTheme.typography.titleLarge, color = KaguraText)
                s.perType.forEach { tp ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tp.type.label, color = KaguraText, style = MaterialTheme.typography.bodyMedium)
                            Text("${tp.done}/${tp.total}", color = KaguraMuted, style = MaterialTheme.typography.labelSmall)
                        }
                        LinearProgressIndicator(
                            progress = { tp.fraction },
                            color = KaguraViolet,
                            trackColor = KaguraSurfaceHigh,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier, backgroundColor = KaguraGlassSurface) {
        Column(Modifier.padding(14.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = color)
            Spacer(Modifier.height(4.dp))
            Text(label, color = KaguraMuted, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
        }
    }
}
