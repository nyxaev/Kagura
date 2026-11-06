package com.kagura.app.data.remote.anilist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, kotlinx.serialization.json.JsonElement> = emptyMap()
)

@Serializable
data class AniListGraphQLResponse(
    val data: AniListData? = null
)

@Serializable
data class AniListData(
    @SerialName("Page")
    val page: AniListPage? = null
)

@Serializable
data class AniListPage(
    val pageInfo: AniListPageInfo? = null,
    val media: List<AniListMediaItem> = emptyList()
)

@Serializable
data class AniListPageInfo(
    val hasNextPage: Boolean = false,
    val currentPage: Int = 1
)

@Serializable
data class AniListMediaItem(
    val id: Int,
    val title: AniListTitle? = null,
    val type: String? = null,
    val genres: List<String> = emptyList(),
    val description: String? = null,
    val averageScore: Int? = null,
    val popularity: Int? = null,
    val episodes: Int? = null,
    val chapters: Int? = null,
    val coverImage: AniListCoverImage? = null,
    val bannerImage: String? = null,
    val studio: AniListStudios? = null,
    val startDate: AniListFuzzyDate? = null
)

@Serializable
data class AniListFuzzyDate(
    val year: Int? = null
)

@Serializable
data class AniListTitle(
    val romaji: String? = null,
    val english: String? = null,
    val native: String? = null
)

@Serializable
data class AniListCoverImage(
    val large: String? = null,
    val medium: String? = null
)

@Serializable
data class AniListStudios(
    val nodes: List<AniListStudioNode> = emptyList()
)

@Serializable
data class AniListStudioNode(
    val name: String
)
