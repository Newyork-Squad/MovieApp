package com.karrar.movieapp.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getSavedLanguageCodeFlow(): Flow<String?>
    suspend fun saveLanguageCode(code: String)

    fun isDarkMode(): Flow<Boolean>
    suspend fun saveDarkMode(enabled: Boolean)
}