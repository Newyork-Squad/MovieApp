package com.karrar.movieapp.ui.profile.contentPreferences

import com.karrar.movieapp.ml.StrengthLevel


sealed class ContentPreferenceEvent {
    object CloseDialog : ContentPreferenceEvent()
    data class PreferenceSelected(val level: StrengthLevel) : ContentPreferenceEvent()
}