package com.kagura.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKey(id: String): RemoteKeysEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(keys: List<RemoteKeysEntity>)

    @Query("DELETE FROM remote_keys WHERE id = :id")
    suspend fun deleteKey(id: String)

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}
