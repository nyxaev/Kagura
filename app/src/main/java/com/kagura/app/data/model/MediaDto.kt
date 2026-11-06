package com.kagura.app.data.model

import com.kagura.app.data.local.MediaEntity
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.model.Raridade
import com.kagura.app.domain.usecase.RarityCalculator

data class MediaDto(
    val id: Int,
    val title: String,
    val type: MediaType,
    val synopsis: String?,
    val coverUrl: String?,
    val score: Float,
    val vibe: String?,
    val units: Int?,
    val year: Int?
)

private fun rarityFromScore(score: Float): Raridade = RarityCalculator.calculate(score, 10000)

@Suppress("unused")
fun MediaDto.toEntity() = MediaEntity(
    id = id, title = title, type = type.name, synopsis = synopsis.orEmpty(),
    coverUrl = coverUrl.orEmpty(), rarity = rarityFromScore(score).name,
    status = ItemStatus.NONE.name, userRating = null, score = score,
    vibe = vibe ?: "Aventura", tears = 0, units = units ?: 1, year = year ?: 0,
    accent = 0xFFB86BFF
)
