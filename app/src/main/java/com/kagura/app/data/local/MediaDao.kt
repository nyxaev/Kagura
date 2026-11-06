package com.kagura.app.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media ORDER BY title")
    fun observeAll(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media")
    suspend fun getAll(): List<MediaEntity>

    @Query("SELECT * FROM media WHERE id = :id")
    fun observeById(id: Int): Flow<MediaEntity?>

    @Query("SELECT * FROM media WHERE id = :id")
    suspend fun getById(id: Int): MediaEntity?

    @Query("SELECT * FROM media WHERE type = :type ORDER BY popularity DESC")
    fun pagingSourceByType(type: String): PagingSource<Int, MediaEntity>

    @Query("SELECT media.* FROM media JOIN media_fts ON media.id = media_fts.rowid WHERE media_fts MATCH :match ORDER BY media.title")
    fun search(match: String): Flow<List<MediaEntity>>

    @Query("SELECT media.* FROM media JOIN media_fts ON media.id = media_fts.rowid WHERE media_fts MATCH :match ORDER BY media.title")
    suspend fun searchList(match: String): List<MediaEntity>

    @Query("SELECT COUNT(*) FROM media")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MediaEntity>)

    @Query("UPDATE media SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("UPDATE media SET userRating = :rating WHERE id = :id")
    suspend fun updateRating(id: Int, rating: Float?)

    @Query("DELETE FROM media WHERE source = :source")
    suspend fun clearBySource(source: String)

    @Transaction
    suspend fun upsertPreservingUserState(newItems: List<MediaEntity>, userStateDao: UserStateDao) {
        newItems.forEach { newItem ->
            val existingState = userStateDao.getByMediaId(newItem.id)
            val existingMedia = getById(newItem.id)

            val finalStatus = existingState?.status
                ?: (if (existingMedia != null && existingMedia.status != "NONE") existingMedia.status else newItem.status)
            val finalRating = existingState?.userRating
                ?: existingMedia?.userRating
                ?: newItem.userRating

            val mergedItem = newItem.copy(
                status = finalStatus,
                userRating = finalRating
            )
            insertAll(listOf(mergedItem))
        }
    }
}
