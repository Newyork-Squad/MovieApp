package com.karrar.movieapp.domain.usecases.setting

import com.karrar.movieapp.data.repository.SettingsRepository
import com.karrar.movieapp.ml.StrengthLevel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContentPreferenceUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<StrengthLevel> = repository.getPreference()
}