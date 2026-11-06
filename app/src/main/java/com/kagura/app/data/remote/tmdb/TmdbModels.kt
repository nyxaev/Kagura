package com.kagura.app.data.remote.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbPageResponse<T>(
    val page: Int = 1,
    val results: List<T> = emptyList(),
    @SerialName("total_pages")
    val totalPages: Int = 1,
    @SerialName("total_results")
    val totalResults: Int = 0
)

@Serializable
data class TmdbMovieItem(
    val id: Int,
    val title: String = "",
    @SerialName("original_title")
    val originalTitle: String? = null,
    val overview: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    val popularity: Double = 0.0,
    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList()
)

@Serializable
data class TmdbMovieDetail(
    val id: Int,
    val title: String = "",
    @SerialName("original_title")
    val originalTitle: String? = null,
    val overview: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    val popularity: Double = 0.0,
    val runtime: Int? = null,
    val genres: List<TmdbGenre> = emptyList(),
    val videos: TmdbVideoResponse? = null,
    val credits: TmdbCreditsResponse? = null
)

@Serializable
data class TmdbGenre(
    val id: Int,
    val name: String
)

@Serializable
data class TmdbVideoResponse(
    val results: List<TmdbVideo> = emptyList()
)

@Serializable
data class TmdbVideo(
    val key: String,
    val site: String,
    val type: String
)

@Serializable
data class TmdbCreditsResponse(
    val cast: List<TmdbCastMember> = emptyList()
)

@Serializable
data class TmdbCastMember(
    val name: String,
    val character: String? = null
)
