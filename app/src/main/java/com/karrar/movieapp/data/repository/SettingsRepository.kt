package com.karrar.movieapp.data.repository

import com.karrar.movieapp.ml.StrengthLevel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun savePreference(level: StrengthLevel)
    fun getPreference(): Flow<StrengthLevel>
}