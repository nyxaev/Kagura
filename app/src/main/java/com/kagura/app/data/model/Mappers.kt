package com.kagura.app.data.model

import com.kagura.app.data.local.MediaEntity
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.model.Raridade

private inline fun <reified T : Enum<T>> String.toEnum(default: T): T =
    enumValues<T>().firstOrNull { it.name == this } ?: default

fun MediaEntity.toDomain() = MediaItem(
    id = id,
    title = title,
    japaneseTitle = japaneseTitle,
    type = type.toEnum(MediaType.ANIME),
    synopsis = synopsis,
    coverUrl = coverUrl,
    rarity = rarity.toEnum(Raridade.COMUM),
    status = status.toEnum(ItemStatus.NONE),
    userRating = userRating,
    score = score,
    vibe = vibe,
    tears = tears,
    units = units,
    year = year,
    accent = accent,
    genre = genre,
    genres = if (genres.isBlank()) emptyList() else genres.split(",").map { it.trim() },
    externalId = externalId,
    source = source,
    popularity = popularity
)

fun MediaItem.toEntity() = MediaEntity(
    id = id,
    title = title,
    japaneseTitle = japaneseTitle,
    type = type.name,
    synopsis = synopsis,
    coverUrl = coverUrl,
    rarity = rarity.name,
    status = status.name,
    userRating = userRating,
    score = score,
    vibe = vibe,
    tears = tears,
    units = units,
    year = year,
    accent = accent,
    genre = genre,
    genres = genres.joinToString(", "),
    externalId = externalId,
    source = source,
    popularity = popularity
)
