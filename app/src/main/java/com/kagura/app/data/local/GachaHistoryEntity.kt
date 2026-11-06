package com.kagura.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gacha_history")
data class GachaHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mediaId: Int,
    val obtainedAt: Long = System.currentTimeMillis(),
    val pityCountAtPull: Int,
    val rarity: String
)
