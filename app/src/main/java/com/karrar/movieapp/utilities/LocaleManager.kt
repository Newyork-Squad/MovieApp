package com.karrar.movieapp.utilities

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManager {
    fun applyLanguageTag(languageTag: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
    fun normalizeDeviceLanguage(deviceLang: String?): String {
        return when (deviceLang) {
            "ar" -> "ar"
            else -> "en"
        }
    }
}