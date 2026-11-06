package com.kagura.app.di

import android.content.Context
import androidx.room.Room
import com.kagura.app.data.local.AppDatabase
import com.kagura.app.data.local.GachaHistoryDao
import com.kagura.app.data.local.MIGRATION_1_2
import com.kagura.app.data.local.MIGRATION_2_3
import com.kagura.app.data.local.MIGRATION_3_4
import com.kagura.app.data.local.MediaDao
import com.kagura.app.data.local.RemoteKeysDao
import com.kagura.app.data.local.UserStateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "kagura.db"
        )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideMediaDao(database: AppDatabase): MediaDao = database.mediaDao()

    @Provides
    fun provideUserStateDao(database: AppDatabase): UserStateDao = database.userStateDao()

    @Provides
    fun provideGachaHistoryDao(database: AppDatabase): GachaHistoryDao = database.gachaHistoryDao()

    @Provides
    fun provideRemoteKeysDao(database: AppDatabase): RemoteKeysDao = database.remoteKeysDao()
}
