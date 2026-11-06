package com.kagura.app.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.kagura.app.data.local.AppDatabase
import com.kagura.app.data.local.MediaEntity
import com.kagura.app.data.local.RemoteKeysEntity
import com.kagura.app.data.remote.anilist.AniListApi
import com.kagura.app.data.remote.anilist.GraphQLRequest
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.usecase.RarityCalculator
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

@OptIn(ExperimentalPagingApi::class)
class MediaRemoteMediator(
    private val mediaType: MediaType,
    private val db: AppDatabase,
    private val aniListApi: AniListApi
) : RemoteMediator<Int, MediaEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MediaEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val fetchedEntities = when (mediaType) {
                MediaType.ANIME -> fetchAniList(page, "ANIME")
                MediaType.MANGA -> fetchAniList(page, "MANGA")
            }

            val endOfPaginationReached = fetchedEntities.isEmpty()

            val prevPage = if (page == 1) null else page - 1
            val nextPage = if (endOfPaginationReached) null else page + 1
            val keyId = "${mediaType.name}_keys"

            db.mediaDao().upsertPreservingUserState(fetchedEntities, db.userStateDao())
            db.remoteKeysDao().insertKeys(
                listOf(
                    RemoteKeysEntity(
                        id = keyId,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                )
            )

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun fetchAniList(page: Int, typeStr: String): List<MediaEntity> {
        val query = """
            query (${'$'}page: Int, ${'$'}perPage: Int, ${'$'}type: MediaType) {
              Page(page: ${'$'}page, perPage: ${'$'}perPage) {
                pageInfo { hasNextPage currentPage }
                media(type: ${'$'}type, sort: [POPULARITY_DESC, SCORE_DESC]) {
                  id
                  title { romaji english native }
                  type
                  genres
                  description
                  averageScore
                  popularity
                  episodes
                  chapters
                  coverImage { large medium }
                  bannerImage
                  startDate { year }
                }
              }
            }
        """.trimIndent()

        val variables = buildJsonObject {
            put("page", JsonPrimitive(page))
            put("perPage", JsonPrimitive(30))
            put("type", JsonPrimitive(typeStr))
        }

        val response = aniListApi.queryMedia(GraphQLRequest(query, variables))
        val items = response.data?.page?.media ?: emptyList()

        return items.map { item ->
            val scoreVal = (item.averageScore ?: 70) / 10f
            val pop = item.popularity ?: 5000
            val calculatedRarity = RarityCalculator.calculate(scoreVal, pop)

            val genreStr = item.genres.joinToString(", ")
            val titleStr = item.title?.english ?: item.title?.romaji ?: "Sem título"
            val jpTitle = item.title?.native ?: ""
            val img = item.coverImage?.large ?: item.coverImage?.medium ?: ""
            val unitsVal = if (typeStr == "ANIME") item.episodes ?: 12 else item.chapters ?: 50
            val primaryGenre = item.genres.firstOrNull() ?: "Outros"

            MediaEntity(
                id = if (typeStr == "MANGA") 300000 + item.id else 100000 + item.id,
                title = titleStr,
                japaneseTitle = jpTitle,
                type = if (typeStr == "ANIME") "ANIME" else "MANGA",
                synopsis = item.description?.replace(Regex("<[^>]*>"), "") ?: "Sem sinopse disponível.",
                coverUrl = img,
                rarity = calculatedRarity.name,
                status = "NONE",
                userRating = null,
                score = scoreVal,
                vibe = primaryGenre,
                genre = primaryGenre,
                tears = if (scoreVal >= 8.0f) 5 else 3,
                units = unitsVal,
                year = item.startDate?.year ?: 0,
                accent = if (typeStr == "MANGA") 0xFF7FD6F5 else 0xFFE056FD,
                genres = genreStr,
                externalId = item.id.toString(),
                source = "anilist",
                lastSyncedAt = System.currentTimeMillis(),
                popularity = pop
            )
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MediaEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            db.remoteKeysDao().getRemoteKey("${mediaType.name}_keys")
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MediaEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            db.remoteKeysDao().getRemoteKey("${mediaType.name}_keys")
        }
    }

    private suspend fun getRemoteKeyClosestToPosition(state: PagingState<Int, MediaEntity>): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                db.remoteKeysDao().getRemoteKey("${mediaType.name}_keys")
            }
        }
    }
}
