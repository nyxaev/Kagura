package com.kagura.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kagura.app.data.repository.KaguraRepository
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.model.Raridade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TypeProgress(val type: MediaType, val done: Int, val total: Int) {
    val fraction: Float get() = if (total == 0) 0f else done / total.toFloat()
}

data class ProfileStats(
    val total: Int = 0,
    val completed: Int = 0,
    val progress: Float = 0f,
    val perType: List<TypeProgress> = emptyList(),
    val savedEpisodes: Int = 0,
    val savedChapters: Int = 0,
    val want: Int = 0,
    val inProgress: Int = 0,
    val legendaryDone: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    repo: KaguraRepository
) : ViewModel() {
    val stats: StateFlow<ProfileStats> = repo.observeAll().map { all ->
        val done = all.count { it.status == ItemStatus.COMPLETED }
        val saved = all.filter { it.status != ItemStatus.NONE }
        ProfileStats(
            total = all.size, completed = done,
            progress = if (all.isEmpty()) 0f else done / all.size.toFloat(),
            perType = listOf(MediaType.ANIME, MediaType.MANGA).map { t ->
                val l = all.filter { it.type == t }
                TypeProgress(t, l.count { it.status == ItemStatus.COMPLETED }, l.size)
            },
            savedEpisodes = saved.filter { it.type == MediaType.ANIME }.sumOf { it.units },
            savedChapters = saved.filter { it.type == MediaType.MANGA }.sumOf { it.units },
            want = all.count { it.status == ItemStatus.WANT },
            inProgress = all.count { it.status == ItemStatus.IN_PROGRESS },
            legendaryDone = all.count { it.status == ItemStatus.COMPLETED && it.rarity == Raridade.LENDARIO }
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileStats())
}
