package com.karrar.movieapp.domain.usecases.setting

import com.karrar.movieapp.data.repository.SettingsRepository
import javax.inject.Inject

class SaveLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(language: String) {
        repository.saveLanguageCode(language)
    }
}