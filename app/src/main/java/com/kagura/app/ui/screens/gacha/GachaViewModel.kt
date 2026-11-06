package com.kagura.app.ui.screens.gacha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kagura.app.data.repository.KaguraRepository
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.usecase.GachaSource
import com.kagura.app.domain.usecase.GetRandomGachaItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GachaPhase { IDLE, ROLLING, REVEAL }

data class GachaUiState(
    val source: GachaSource = GachaSource.CATALOG,
    val mediaType: MediaType? = null,
    val genre: String? = null,
    val phase: GachaPhase = GachaPhase.IDLE,
    val result: MediaItem? = null,
    val revealed: Boolean = false,
    val empty: Boolean = false,
    val userFavGenres: List<String> = emptyList(),
    val totalAvailableCount: Int = 0
)

@HiltViewModel
class GachaViewModel @Inject constructor(
    private val repo: KaguraRepository,
    private val randomItem: GetRandomGachaItemUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(GachaUiState())
    val state: StateFlow<GachaUiState> = _state.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            repo.syncRemoteData()
            val favs = repo.getUserFavoriteGenres()
            val total = repo.getAll().size
            _state.update { it.copy(userFavGenres = favs, totalAvailableCount = total) }
        }
    }

    fun setSource(s: GachaSource) = _state.update { it.copy(source = s, empty = false) }
    fun setMediaType(t: MediaType?) = _state.update { it.copy(mediaType = t, empty = false) }
    fun setGenre(g: String?) = _state.update { it.copy(genre = g, empty = false) }

    fun roll() {
        if (_state.value.phase == GachaPhase.ROLLING) return
        viewModelScope.launch {
            _state.update { it.copy(phase = GachaPhase.ROLLING, result = null, revealed = false, empty = false) }
            delay(1000)
            val s = _state.value
            val item = randomItem(s.source, s.mediaType, s.genre)
            if (item == null) {
                _state.update { it.copy(phase = GachaPhase.IDLE, empty = true) }
            } else {
                val total = repo.getAll().size
                val favs = repo.getUserFavoriteGenres()
                _state.update {
                    it.copy(
                        phase = GachaPhase.REVEAL,
                        result = item,
                        revealed = false,
                        totalAvailableCount = total,
                        userFavGenres = favs
                    )
                }
                repo.recordGachaPull(item.id, 0, item.rarity.name)
                delay(400)
                _state.update { it.copy(revealed = true) }
            }
        }
    }

    fun addWant() {
        val r = _state.value.result ?: return
        viewModelScope.launch {
            repo.setStatus(r.id, ItemStatus.WANT)
            _state.update { it.copy(result = r.copy(status = ItemStatus.WANT)) }
        }
    }
}
