package com.kagura.app.data.local

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(contentEntity = MediaEntity::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity(tableName = "media_fts")
data class MediaFts(
    val title: String,
    val japaneseTitle: String,
    val synopsis: String,
    val genres: String,
    val vibe: String
)
