package com.kagura.app.ui.screens.dex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kagura.app.data.repository.KaguraRepository
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.model.Raridade
import com.kagura.app.domain.usecase.FilterDexUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DexUiState(
    val type: MediaType = MediaType.ANIME,
    val rarity: Raridade? = null,
    val items: List<MediaItem> = emptyList(),
    val progress: Float = 0f,
    val done: Int = 0,
    val total: Int = 0
)

@HiltViewModel
class DexViewModel @Inject constructor(
    repo: KaguraRepository,
    private val filter: FilterDexUseCase
) : ViewModel() {
    private val type = MutableStateFlow(MediaType.ANIME)
    private val rarity = MutableStateFlow<Raridade?>(null)

    val state: StateFlow<DexUiState> = combine(repo.observeAll(), type, rarity) { all, t, r ->
        val ofType = all.filter { it.type == t }
        DexUiState(t, r, filter(all, t, r), filter.progress(all, t), ofType.count { it.status == ItemStatus.COMPLETED }, ofType.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DexUiState())

    fun setType(t: MediaType) { type.value = t }
    fun setRarity(r: Raridade?) { rarity.value = r }
}
