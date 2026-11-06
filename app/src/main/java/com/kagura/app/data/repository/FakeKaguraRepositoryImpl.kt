package com.kagura.app.data.repository

import androidx.paging.PagingData
import com.kagura.app.data.local.GachaHistoryEntity
import com.kagura.app.data.local.MediaDao
import com.kagura.app.data.local.SeedData
import com.kagura.app.data.model.toDomain
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FakeKaguraRepositoryImpl @Inject constructor(private val dao: MediaDao) : KaguraRepository {
    override fun observeAll(): Flow<List<MediaItem>> = dao.observeAll().map { l -> l.map { it.toDomain() } }

    override fun observeById(id: Int): Flow<MediaItem?> = dao.observeById(id).map { it?.toDomain() }

    override fun search(query: String): Flow<List<MediaItem>> {
        val tokens = query.split(" ").map { t -> t.filter { it.isLetterOrDigit() } }.filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return observeAll()
        return dao.search(tokens.joinToString(" ") { "$it*" }).map { l -> l.map { it.toDomain() } }
    }

    override suspend fun searchSync(query: String): List<MediaItem> = searchLocalOnly(query)

    override suspend fun searchLocalOnly(query: String): List<MediaItem> {
        val tokens = query.split(" ").map { t -> t.filter { it.isLetterOrDigit() } }.filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return getAll()
        return dao.searchList(tokens.joinToString(" ") { "$it*" }).map { it.toDomain() }
    }

    override fun getPagedMedia(type: MediaType): Flow<PagingData<MediaItem>> = flowOf(PagingData.empty())

    override suspend fun getAll(): List<MediaItem> = dao.getAll().map { it.toDomain() }
    override suspend fun setStatus(id: Int, status: ItemStatus) = dao.updateStatus(id, status.name)
    override suspend fun setRating(id: Int, rating: Float?) = dao.updateRating(id, rating)
    override suspend fun seedIfEmpty() { if (dao.count() == 0) dao.insertAll(SeedData.items()) }

    override suspend fun recordGachaPull(mediaId: Int, pityCountAtPull: Int, rarity: String) {}
    override fun observeGachaHistory(): Flow<List<GachaHistoryEntity>> = flowOf(emptyList())
    override suspend fun syncRemoteData() { seedIfEmpty() }
    override suspend fun fetchCatalogFromRemote(mediaType: MediaType?, genre: String?): Int = 0
    override suspend fun getUserFavoriteGenres(): List<String> = listOf("Ação", "Aventura", "Romance")
}
