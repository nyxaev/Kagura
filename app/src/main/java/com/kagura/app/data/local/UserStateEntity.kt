package com.kagura.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_state")
data class UserStateEntity(
    @PrimaryKey val mediaId: Int,
    val status: String,
    val userRating: Float?,
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String? = null
)
