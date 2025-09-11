package com.karrar.movieapp.ui.profile

sealed interface ProfileUIEvent {
    object LoginEvent : ProfileUIEvent
    object RatedMoviesEvent : ProfileUIEvent
    object DialogLogoutEvent : ProfileUIEvent
    object WatchHistoryEvent : ProfileUIEvent
    object MyCollectionsEvent : ProfileUIEvent
    object EditProfileEvent : ProfileUIEvent
    object ShowLanguagePicker : ProfileUIEvent
    object ShowContentPreferences : ProfileUIEvent
}
