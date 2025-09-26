package com.karrar.movieapp.domain.usecases.setting

import com.karrar.movieapp.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDarkModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isDarkMode()
}