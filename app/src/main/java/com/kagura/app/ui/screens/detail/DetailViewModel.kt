package com.kagura.app.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kagura.app.data.repository.KaguraRepository
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repo: KaguraRepository,
    handle: SavedStateHandle
) : ViewModel() {
    private val id: Int = handle.get<Int>("id") ?: 0
    val item: StateFlow<MediaItem?> = repo.observeById(id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setStatus(s: ItemStatus) { viewModelScope.launch { repo.setStatus(id, s) } }
    fun setRating(r: Float) { viewModelScope.launch { repo.setRating(id, if (r == 0f) null else r) } }
}
