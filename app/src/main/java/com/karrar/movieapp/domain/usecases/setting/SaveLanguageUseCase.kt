package com.karrar.movieapp.domain.usecases.setting

import com.karrar.movieapp.data.repository.AccountRepository
import javax.inject.Inject

class SaveLanguageUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(language: String) {
        repository.saveLanguage(language)
    }
}