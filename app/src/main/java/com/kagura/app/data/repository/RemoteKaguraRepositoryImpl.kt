package com.kagura.app.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kagura.app.data.local.AppDatabase
import com.kagura.app.data.local.GachaHistoryDao
import com.kagura.app.data.local.GachaHistoryEntity
import com.kagura.app.data.local.MediaDao
import com.kagura.app.data.local.MediaEntity
import com.kagura.app.data.local.SeedData
import com.kagura.app.data.local.UserStateDao
import com.kagura.app.data.local.UserStateEntity
import com.kagura.app.data.model.toDomain
import com.kagura.app.data.paging.MediaRemoteMediator
import com.kagura.app.data.remote.anilist.AniListApi
import com.kagura.app.data.remote.anilist.GraphQLRequest
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.usecase.RarityCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteKaguraRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val mediaDao: MediaDao,
    private val userStateDao: UserStateDao,
    private val gachaHistoryDao: GachaHistoryDao,
    private val aniListApi: AniListApi
) : KaguraRepository {

    override fun observeAll(): Flow<List<MediaItem>> =
        mediaDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Int): Flow<MediaItem?> =
        mediaDao.observeById(id).map { it?.toDomain() }

    override fun search(query: String): Flow<List<MediaItem>> = flow {
        val tokens = query.split(" ")
            .map { t -> t.filter { it.isLetterOrDigit() } }
            .filter { it.isNotEmpty() }
        if (tokens.isEmpty()) {
            emitAll(observeAll())
            return@flow
        }
        val matchQuery = tokens.joinToString(" ") { "$it*" }
        val localResults = mediaDao.searchList(matchQuery).map { it.toDomain() }
        emit(localResults)

        try {
            val remoteResults = searchSync(query)
            emit(remoteResults)
        } catch (e: Exception) {
            emit(localResults)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun searchSync(query: String): List<MediaItem> = withContext(Dispatchers.IO) {
        val tokens = query.split(" ")
            .map { t -> t.filter { it.isLetterOrDigit() } }
            .filter { it.isNotEmpty() }
        val matchQuery = if (tokens.isNotEmpty()) tokens.joinToString(" ") { "$it*" } else query

        coroutineScope {
            val anilistDeferred = async {
                try {
                    val gql = GraphQLRequest(
                        query = "query (\$search: String) { Page(page: 1, perPage: 20) { media(type: ANIME, search: \$search) { id title { romaji english native } type genres description averageScore popularity episodes chapters coverImage { large medium } bannerImage } } }",
                        variables = mapOf("search" to kotlinx.serialization.json.JsonPrimitive(query))
                    )
                    val resp = aniListApi.queryMedia(gql)
                    resp.data?.page?.media?.map { item ->
                        val scoreVal = (item.averageScore ?: 70) / 10f
                        val pop = item.popularity ?: 5000
                        val calculatedRarity = RarityCalculator.calculate(scoreVal, pop)
                        val genreStr = item.genres.joinToString(", ")
                        val primaryGenre = item.genres.firstOrNull() ?: "Outros"
                        val titleStr = item.title?.english ?: item.title?.romaji ?: "Sem título"
                        val img = item.coverImage?.large ?: item.coverImage?.medium ?: ""
                        MediaEntity(
                            id = 100000 + item.id,
                            title = titleStr,
                            japaneseTitle = item.title?.native ?: "",
                            type = "ANIME",
                            synopsis = item.description?.replace(Regex("<[^>]*>"), "") ?: "Sem sinopse.",
                            coverUrl = img,
                            rarity = calculatedRarity.name,
                            status = "NONE",
                            userRating = null,
                            score = scoreVal,
                            vibe = primaryGenre,
                            tears = if (scoreVal >= 8.0f) 5 else 3,
                            units = item.episodes ?: 12,
                            year = 2023,
                            accent = 0xFFE056FD,
                            genre = primaryGenre,
                            genres = genreStr,
                            externalId = item.id.toString(),
                            source = "anilist",
                            popularity = pop
                        )
                    } ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
            }

            val remoteEntities = anilistDeferred.await()
            if (remoteEntities.isNotEmpty()) {
                mediaDao.upsertPreservingUserState(remoteEntities, userStateDao)
            }

            val localResults = mediaDao.searchList(matchQuery).map { it.toDomain() }
            val remoteResults = remoteEntities.map { it.toDomain() }
            (localResults + remoteResults).distinctBy { it.id }
        }
    }

    override suspend fun searchLocalOnly(query: String): List<MediaItem> = withContext(Dispatchers.IO) {
        val tokens = query.split(" ")
            .map { t -> t.filter { it.isLetterOrDigit() } }
            .filter { it.isNotEmpty() }
        val matchQuery = if (tokens.isNotEmpty()) tokens.joinToString(" ") { "$it*" } else query
        mediaDao.searchList(matchQuery).map { it.toDomain() }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedMedia(type: MediaType): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = MediaRemoteMediator(type, db, aniListApi),
            pagingSourceFactory = { mediaDao.pagingSourceByType(type.name) }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override suspend fun getAll(): List<MediaItem> =
        mediaDao.getAll().map { it.toDomain() }

    override suspend fun setStatus(id: Int, status: ItemStatus) {
        mediaDao.updateStatus(id, status.name)
        val currentState = userStateDao.getByMediaId(id)
        val newState = UserStateEntity(
            mediaId = id,
            status = status.name,
            userRating = currentState?.userRating,
            updatedAt = System.currentTimeMillis()
        )
        userStateDao.upsert(newState)
    }

    override suspend fun setRating(id: Int, rating: Float?) {
        mediaDao.updateRating(id, rating)
        val currentState = userStateDao.getByMediaId(id)
        val currentStatus = currentState?.status ?: "NONE"
        val newState = UserStateEntity(
            mediaId = id,
            status = currentStatus,
            userRating = rating,
            updatedAt = System.currentTimeMillis()
        )
        userStateDao.upsert(newState)
    }

    override suspend fun seedIfEmpty() {
        if (mediaDao.count() == 0) {
            mediaDao.insertAll(SeedData.items())
        }
    }

    override suspend fun recordGachaPull(mediaId: Int, pityCountAtPull: Int, rarity: String) {
        gachaHistoryDao.insert(
            GachaHistoryEntity(
                mediaId = mediaId,
                obtainedAt = System.currentTimeMillis(),
                pityCountAtPull = pityCountAtPull,
                rarity = rarity
            )
        )
    }

    override fun observeGachaHistory(): Flow<List<GachaHistoryEntity>> =
        gachaHistoryDao.observeAllHistory()

    override suspend fun syncRemoteData() {
        seedIfEmpty()
        if (mediaDao.count() < 80) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    fetchCatalogFromRemote()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override suspend fun fetchCatalogFromRemote(mediaType: MediaType?, genre: String?): Int = withContext(Dispatchers.IO) {
        seedIfEmpty()
        val entities = mutableListOf<MediaEntity>()

        coroutineScope {
            if (mediaType == null || mediaType == MediaType.ANIME) {
                launch {
                    try {
                        val genreVar = if (!genre.isNullOrBlank() && !genre.equals("Todos os gêneros", ignoreCase = true)) genre else null
                        for (p in 1..2) {
                            val gql = GraphQLRequest(
                                query = """
                                    query (${'$'}page: Int, ${'$'}genre: String) {
                                      Page(page: ${'$'}page, perPage: 30) {
                                        media(type: ANIME, genre: ${'$'}genre, sort: [POPULARITY_DESC, SCORE_DESC]) {
                                          id
                                          title { romaji english native }
                                          type
                                          genres
                                          description
                                          averageScore
                                          popularity
                                          episodes
                                          coverImage { large medium }
                                        }
                                      }
                                    }
                                """.trimIndent(),
                                variables = buildMap {
                                    put("page", kotlinx.serialization.json.JsonPrimitive(p))
                                    if (genreVar != null) put("genre", kotlinx.serialization.json.JsonPrimitive(genreVar))
                                }
                            )
                            val resp = aniListApi.queryMedia(gql)
                            resp.data?.page?.media?.forEach { item ->
                                val scoreVal = (item.averageScore ?: 70) / 10f
                                val pop = item.popularity ?: 5000
                                val calculatedRarity = RarityCalculator.calculate(scoreVal, pop)
                                val genreStr = item.genres.joinToString(", ")
                                val primaryGenre = item.genres.firstOrNull() ?: "Animes"
                                val titleStr = item.title?.english ?: item.title?.romaji ?: "Sem título"
                                val img = item.coverImage?.large ?: item.coverImage?.medium ?: ""
                                entities.add(
                                    MediaEntity(
                                        id = 100000 + item.id,
                                        title = titleStr,
                                        japaneseTitle = item.title?.native ?: "",
                                        type = "ANIME",
                                        synopsis = item.description?.replace(Regex("<[^>]*>"), "") ?: "Sem sinopse.",
                                        coverUrl = img,
                                        rarity = calculatedRarity.name,
                                        status = "NONE",
                                        userRating = null,
                                        score = scoreVal,
                                        vibe = primaryGenre,
                                        tears = if (scoreVal >= 8.0f) 5 else 3,
                                        units = item.episodes ?: 12,
                                        year = 2023,
                                        accent = 0xFFE056FD,
                                        genre = primaryGenre,
                                        genres = genreStr,
                                        externalId = item.id.toString(),
                                        source = "anilist",
                                        popularity = pop
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            if (mediaType == null || mediaType == MediaType.MANGA) {
                launch {
                    try {
                        val genreVar = if (!genre.isNullOrBlank() && !genre.equals("Todos os gêneros", ignoreCase = true)) genre else null
                        for (p in 1..2) {
                            val gql = GraphQLRequest(
                                query = """
                                    query (${'$'}page: Int, ${'$'}genre: String) {
                                      Page(page: ${'$'}page, perPage: 30) {
                                        media(type: MANGA, genre: ${'$'}genre, sort: [POPULARITY_DESC, SCORE_DESC]) {
                                          id
                                          title { romaji english native }
                                          type
                                          genres
                                          description
                                          averageScore
                                          popularity
                                          chapters
                                          coverImage { large medium }
                                        }
                                      }
                                    }
                                """.trimIndent(),
                                variables = buildMap {
                                    put("page", kotlinx.serialization.json.JsonPrimitive(p))
                                    if (genreVar != null) put("genre", kotlinx.serialization.json.JsonPrimitive(genreVar))
                                }
                            )
                            val resp = aniListApi.queryMedia(gql)
                            resp.data?.page?.media?.forEach { item ->
                                val scoreVal = (item.averageScore ?: 70) / 10f
                                val pop = item.popularity ?: 5000
                                val calculatedRarity = RarityCalculator.calculate(scoreVal, pop)
                                val genreStr = item.genres.joinToString(", ")
                                val primaryGenre = item.genres.firstOrNull() ?: "Mangás"
                                val titleStr = item.title?.english ?: item.title?.romaji ?: "Sem título"
                                val img = item.coverImage?.large ?: item.coverImage?.medium ?: ""
                                entities.add(
                                    MediaEntity(
                                        id = 300000 + item.id,
                                        title = titleStr,
                                        japaneseTitle = item.title?.native ?: "",
                                        type = "MANGA",
                                        synopsis = item.description?.replace(Regex("<[^>]*>"), "") ?: "Sem sinopse.",
                                        coverUrl = img,
                                        rarity = calculatedRarity.name,
                                        status = "NONE",
                                        userRating = null,
                                        score = scoreVal,
                                        vibe = primaryGenre,
                                        tears = if (scoreVal >= 8.0f) 5 else 3,
                                        units = item.chapters ?: 50,
                                        year = 2022,
                                        accent = 0xFF7FD6F5,
                                        genre = primaryGenre,
                                        genres = genreStr,
                                        externalId = item.id.toString(),
                                        source = "anilist",
                                        popularity = pop
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        if (entities.isNotEmpty()) {
            mediaDao.upsertPreservingUserState(entities, userStateDao)
        }
        entities.size
    }

    override suspend fun getUserFavoriteGenres(): List<String> = withContext(Dispatchers.IO) {
        val all = mediaDao.getAll()
        val userItems = all.filter {
            it.status == ItemStatus.COMPLETED.name ||
                    it.status == ItemStatus.IN_PROGRESS.name ||
                    it.status == ItemStatus.WANT.name ||
                    (it.userRating != null && it.userRating >= 3.5f)
        }

        if (userItems.isEmpty()) {
            return@withContext listOf("Ação", "Romance", "Aventura", "Comédia", "Drama")
        }

        val genreScores = mutableMapOf<String, Int>()
        userItems.forEach { item ->
            val weight = when {
                item.userRating != null && item.userRating >= 4.5f -> 5
                item.status == ItemStatus.COMPLETED.name -> 4
                item.status == ItemStatus.IN_PROGRESS.name -> 3
                item.status == ItemStatus.WANT.name -> 2
                else -> 1
            }

            val list = item.genres.split(",").map { it.trim() }.filter { it.isNotBlank() } + item.genre
            list.forEach { g ->
                val norm = g.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                genreScores[norm] = (genreScores[norm] ?: 0) + weight
            }
        }

        val top = genreScores.entries.sortedByDescending { it.value }.map { it.key }.take(5)
        if (top.isEmpty()) listOf("Ação", "Romance", "Aventura", "Comédia", "Drama") else top
    }
}
