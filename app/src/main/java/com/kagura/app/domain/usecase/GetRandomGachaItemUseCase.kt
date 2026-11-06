package com.kagura.app.domain.usecase

import com.kagura.app.data.repository.KaguraRepository
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import javax.inject.Inject
import kotlin.random.Random

enum class GachaSource { WANT, CATALOG }

class GetRandomGachaItemUseCase @Inject constructor(
    private val repo: KaguraRepository
) {
    private var random: Random = Random.Default

    constructor(repo: KaguraRepository, random: Random) : this(repo) {
        this.random = random
    }

    suspend operator fun invoke(
        source: GachaSource = GachaSource.CATALOG,
        mediaType: MediaType? = null,
        genre: String? = null
    ): MediaItem? {
        repo.syncRemoteData()

        val initialPool = repo.getAll().filter { item ->
            val matchSource = (source == GachaSource.CATALOG || item.status == ItemStatus.WANT)
            val matchType = (mediaType == null || item.type == mediaType)
            val matchGenre = (genre == null || genre.equals("Todos os gêneros", ignoreCase = true) ||
                    item.genre.equals(genre, ignoreCase = true) ||
                    item.vibe.contains(genre, ignoreCase = true) ||
                    item.genres.any { it.equals(genre, ignoreCase = true) })
            matchSource && matchType && matchGenre
        }

        val pool = if (initialPool.isEmpty() && source == GachaSource.CATALOG) {
            repo.fetchCatalogFromRemote(mediaType, genre)
            repo.getAll().filter { item ->
                val matchType = (mediaType == null || item.type == mediaType)
                val matchGenre = (genre == null || genre.equals("Todos os gêneros", ignoreCase = true) ||
                        item.genre.equals(genre, ignoreCase = true) ||
                        item.vibe.contains(genre, ignoreCase = true) ||
                        item.genres.any { it.equals(genre, ignoreCase = true) })
                matchType && matchGenre
            }
        } else initialPool

        if (pool.isEmpty()) return null
        return selectSmartRecommendation(pool)
    }

    private suspend fun selectSmartRecommendation(pool: List<MediaItem>): MediaItem {
        val userFavGenres = repo.getUserFavoriteGenres()
        if (userFavGenres.isEmpty()) return pool.random(random)

        val weightedCandidates = pool.map { item ->
            var weight = 10.0
            if (userFavGenres.any { fav -> item.genres.any { g -> g.equals(fav, ignoreCase = true) } || item.genre.equals(fav, ignoreCase = true) }) {
                weight += 45.0
            }
            weight += (item.score * 6.0)
            if (item.status == ItemStatus.NONE || item.status == ItemStatus.WANT) {
                weight += 20.0
            }
            item to weight
        }

        val totalWeight = weightedCandidates.sumOf { it.second }
        if (totalWeight <= 0) return pool.random(random)

        var target = random.nextDouble() * totalWeight
        for ((item, weight) in weightedCandidates) {
            target -= weight
            if (target <= 0) return item
        }

        return pool.random(random)
    }
}
