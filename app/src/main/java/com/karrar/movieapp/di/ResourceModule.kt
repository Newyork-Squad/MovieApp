package com.karrar.movieapp.di

import com.karrar.movieapp.data.util.ResourceProviderImpl
import com.karrar.movieapp.domain.util.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ResourceModule {

    @Singleton
    @Binds
    abstract fun bindResourceProvider(
        impl: ResourceProviderImpl
    ): ResourceProvider
}