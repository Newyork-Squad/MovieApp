package com.karrar.movieapp.ui.profile.language

sealed interface LanguageUIEvent {
    data class LanguageSelected(val language: String) : LanguageUIEvent
    object CloseDialogEvent : LanguageUIEvent
}
