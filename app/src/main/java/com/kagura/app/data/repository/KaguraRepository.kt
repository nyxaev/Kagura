package com.kagura.app.data.repository

import androidx.paging.PagingData
import com.kagura.app.data.local.GachaHistoryEntity
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import kotlinx.coroutines.flow.Flow

interface KaguraRepository {
    fun observeAll(): Flow<List<MediaItem>>
    fun observeById(id: Int): Flow<MediaItem?>
    fun search(query: String): Flow<List<MediaItem>>
    suspend fun searchSync(query: String): List<MediaItem>
    suspend fun searchLocalOnly(query: String): List<MediaItem>
    fun getPagedMedia(type: MediaType): Flow<PagingData<MediaItem>>
    suspend fun getAll(): List<MediaItem>
    suspend fun setStatus(id: Int, status: ItemStatus)
    suspend fun setRating(id: Int, rating: Float?)
    suspend fun seedIfEmpty()
    suspend fun recordGachaPull(mediaId: Int, pityCountAtPull: Int, rarity: String)
    fun observeGachaHistory(): Flow<List<GachaHistoryEntity>>
    suspend fun syncRemoteData()
    suspend fun fetchCatalogFromRemote(mediaType: MediaType? = null, genre: String? = null): Int
    suspend fun getUserFavoriteGenres(): List<String>
}
