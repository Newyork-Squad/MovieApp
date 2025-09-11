package com.karrar.movieapp.domain.usecases.setting

import com.karrar.movieapp.data.repository.AccountRepository
import javax.inject.Inject

class SaveDarkModeUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.saveDarkMode(enabled)
    }
}