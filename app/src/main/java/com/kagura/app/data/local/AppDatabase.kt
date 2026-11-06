package com.kagura.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE media ADD COLUMN japaneseTitle TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE media ADD COLUMN genres TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE media ADD COLUMN externalId TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE media ADD COLUMN source TEXT NOT NULL DEFAULT 'local'")
        db.execSQL("ALTER TABLE media ADD COLUMN lastSyncedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE media ADD COLUMN popularity INTEGER NOT NULL DEFAULT 0")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS user_state (
                mediaId INTEGER NOT NULL PRIMARY KEY,
                status TEXT NOT NULL,
                userRating REAL,
                updatedAt INTEGER NOT NULL,
                notes TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS gacha_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                mediaId INTEGER NOT NULL,
                obtainedAt INTEGER NOT NULL,
                pityCountAtPull INTEGER NOT NULL,
                rarity TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS remote_keys (
                id TEXT NOT NULL PRIMARY KEY,
                prevPage INTEGER,
                nextPage INTEGER,
                lastUpdated INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL("DROP TABLE IF EXISTS media_fts")
        db.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS media_fts USING fts4(content='media', title, japaneseTitle, synopsis, genres, vibe, tokenizer=unicode61)")
        db.execSQL("INSERT INTO media_fts(media_fts) VALUES('rebuild')")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE media ADD COLUMN genre TEXT NOT NULL DEFAULT ''")
    }
}

@Database(
    entities = [
        MediaEntity::class,
        MediaFts::class,
        UserStateEntity::class,
        GachaHistoryEntity::class,
        RemoteKeysEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun userStateDao(): UserStateDao
    abstract fun gachaHistoryDao(): GachaHistoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
