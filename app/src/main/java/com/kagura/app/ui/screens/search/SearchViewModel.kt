package com.kagura.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kagura.app.data.repository.KaguraRepository
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val items: List<MediaItem>, val isOffline: Boolean = false) : SearchUiState
    object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: KaguraRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _category = MutableStateFlow("Todos")
    val category: StateFlow<String> = _category.asStateFlow()

    val uiState: StateFlow<SearchUiState> = _query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            flow {
                if (q.trim().length < 2) {
                    emit(SearchUiState.Idle)
                    return@flow
                }
                emit(SearchUiState.Loading)
                try {
                    val results = repo.searchSync(q.trim())
                    if (results.isEmpty()) {
                        emit(SearchUiState.Empty)
                    } else {
                        emit(SearchUiState.Success(results, isOffline = false))
                    }
                } catch (e: Exception) {
                    try {
                        val fallback = repo.searchLocalOnly(q.trim())
                        if (fallback.isNotEmpty()) {
                            emit(SearchUiState.Success(fallback, isOffline = true))
                        } else {
                            emit(SearchUiState.Empty)
                        }
                    } catch (ex: Exception) {
                        emit(SearchUiState.Error(ex.message ?: "Erro desconhecido"))
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState.Idle)

    fun onQuery(q: String) {
        _query.value = q
    }

    fun setCategory(cat: String) {
        _category.value = cat
    }
}
