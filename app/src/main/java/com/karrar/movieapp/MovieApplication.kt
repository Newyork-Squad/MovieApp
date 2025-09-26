package com.karrar.movieapp

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.karrar.movieapp.domain.usecases.setting.GetDarkModeUseCase
import com.karrar.movieapp.domain.usecases.setting.GetLanguageUseCase
import com.karrar.movieapp.utilities.LocaleManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltAndroidApp
class MovieApplication : Application() {

    @Inject lateinit var getDarkModeUseCase: GetDarkModeUseCase
    @Inject lateinit var getLanguageUseCase: GetLanguageUseCase

    override fun onCreate() {
        super.onCreate()

        try {
            val langCode = kotlinx.coroutines.runBlocking { getLanguageUseCase.invoke().first() }
            LocaleManager.applyLanguageTag(langCode)
        } catch (_: Exception) {

        }

        try {
            val initialDark = kotlinx.coroutines.runBlocking { getDarkModeUseCase.invoke().first() }
            val mode = if (initialDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        } catch (_: Exception) {

        }
    }
}