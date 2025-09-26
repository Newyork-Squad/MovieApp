package com.karrar.movieapp.data.repository

import com.karrar.movieapp.ml.StrengthLevel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getSavedLanguageCodeFlow(): Flow<String?>
    suspend fun saveLanguageCode(code: String)

    fun isDarkMode(): Flow<Boolean>
    suspend fun saveDarkMode(enabled: Boolean)

    suspend fun savePreference(level: StrengthLevel)
    fun getPreference(): Flow<StrengthLevel>
}