package com.kagura.app.di

import com.kagura.app.data.repository.KaguraRepository
import com.kagura.app.data.repository.RemoteKaguraRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindKaguraRepository(
        impl: RemoteKaguraRepositoryImpl
    ): KaguraRepository
}
