package com.kagura.app.domain.model

enum class MediaType(val label: String, val singular: String) {
    ANIME("Animes", "Anime"), MANGA("Mangás", "Mangá")
}

enum class ItemStatus { NONE, WANT, IN_PROGRESS, COMPLETED }

enum class Raridade(val label: String, val weight: Int) {
    COMUM("Comum", 60), RARO("Raro", 25), EPICO("Épico", 11), LENDARIO("Lendário", 4)
}

data class MediaItem(
    val id: Int,
    val title: String,
    val japaneseTitle: String = "",
    val type: MediaType,
    val synopsis: String,
    val coverUrl: String,
    val rarity: Raridade,
    val status: ItemStatus,
    val userRating: Float?,
    val score: Float,
    val vibe: String,
    val tears: Int,
    val units: Int,
    val year: Int,
    val accent: Long,
    val genre: String = "",
    val genres: List<String> = emptyList(),
    val externalId: String = "",
    val source: String = "local",
    val popularity: Int = 0
)

object Genres {
    val all = listOf("Ação", "Comédia", "Drama", "Romance", "Terror")
}
