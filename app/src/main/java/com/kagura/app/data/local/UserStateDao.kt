package com.kagura.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStateDao {

    @Query("SELECT * FROM user_state WHERE mediaId = :mediaId")
    suspend fun getByMediaId(mediaId: Int): UserStateEntity?

    @Query("SELECT * FROM user_state WHERE mediaId = :mediaId")
    fun observeByMediaId(mediaId: Int): Flow<UserStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: UserStateEntity)

    @Query("SELECT * FROM user_state")
    suspend fun getAllStates(): List<UserStateEntity>
}
