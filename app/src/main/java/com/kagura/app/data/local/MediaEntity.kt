package com.kagura.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val japaneseTitle: String = "",
    val type: String,
    val synopsis: String,
    val coverUrl: String,
    val rarity: String,
    val status: String,
    val userRating: Float?,
    val score: Float,
    val vibe: String,
    val tears: Int,
    val units: Int,
    val year: Int,
    val accent: Long,
    val genre: String = "",
    val genres: String = "",
    val externalId: String = "",
    val source: String = "local",
    val lastSyncedAt: Long = 0L,
    val popularity: Int = 0
)
