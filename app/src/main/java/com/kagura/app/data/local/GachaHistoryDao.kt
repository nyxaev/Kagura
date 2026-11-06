package com.kagura.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GachaHistoryDao {

    @Query("SELECT * FROM gacha_history ORDER BY obtainedAt DESC")
    fun observeAllHistory(): Flow<List<GachaHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: GachaHistoryEntity)

    @Query("SELECT COUNT(*) FROM gacha_history")
    suspend fun count(): Int
}
