package com.karrar.movieapp.di

import com.karrar.movieapp.data.repository.SettingsRepository
import com.karrar.movieapp.data.repository.SettingsRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Singleton
    @Binds
    abstract fun bindSettingsRepository(
        settingsRepositoryImp: SettingsRepositoryImp
    ): SettingsRepository
}