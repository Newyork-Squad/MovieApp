package com.karrar.movieapp.domain.usecases.setting


import com.karrar.movieapp.data.repository.SettingsRepository
import com.karrar.movieapp.ml.StrengthLevel
import javax.inject.Inject

class SaveContentPreferenceUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(level: StrengthLevel) {
        repository.savePreference(level)
    }
}

