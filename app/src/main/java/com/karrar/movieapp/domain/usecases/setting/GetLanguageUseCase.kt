package com.karrar.movieapp.domain.usecases.setting

import com.karrar.movieapp.data.repository.SettingsRepository
import com.karrar.movieapp.utilities.LocaleManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

class GetLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    fun invoke(): Flow<String> = flow {
        val saved = settingsRepository.getSavedLanguageCodeFlow().first()
        val deviceLang = LocaleManager.normalizeDeviceLanguage(Locale.getDefault().language)

        val initial = saved ?: run {
            settingsRepository.saveLanguageCode(deviceLang)
            deviceLang
        }

        emit(initial)

        emitAll(
            settingsRepository.getSavedLanguageCodeFlow().map { it ?: deviceLang }
        )
    }
}