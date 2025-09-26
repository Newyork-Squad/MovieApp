package com.karrar.movieapp.data.repository

import com.karrar.movieapp.data.local.AppConfiguration
import com.karrar.movieapp.ml.StrengthLevel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImp @Inject constructor(
    private val appConfiguration: AppConfiguration,
) : SettingsRepository, BaseRepository() {

    override suspend fun savePreference(level: StrengthLevel) {
        appConfiguration.saveContentPreference(level)
    }

    override fun getPreference(): Flow<StrengthLevel> =
        appConfiguration.getContentPreference()

}