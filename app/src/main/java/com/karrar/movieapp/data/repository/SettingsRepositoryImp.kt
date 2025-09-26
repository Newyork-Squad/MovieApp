package com.karrar.movieapp.data.repository

import com.karrar.movieapp.data.local.AppConfiguration
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImp @Inject constructor(
    private val appConfiguration: AppConfiguration,
) : SettingsRepository, BaseRepository() {

    override fun getSavedLanguageCodeFlow(): Flow<String?> =
        appConfiguration.getLanguageCodeFlow()


    override suspend fun saveLanguageCode(code: String) {
        appConfiguration.saveLanguage(code)
    }

    override fun isDarkMode(): Flow<Boolean> =
        appConfiguration.isDarkMode()

    override suspend fun saveDarkMode(enabled: Boolean) {
        appConfiguration.saveDarkMode(enabled)
    }
}