package com.karrar.movieapp.ui.search

import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaUIState
import com.karrar.movieapp.ui.search.mediaSearchUIState.RecentMovieViewedUiState

sealed interface SearchUIEvent {
    data class ClickMediaEvent(val mediaUIState: MediaUIState) : SearchUIEvent
    data class ClickRecentViewedEvent(val recentMovieViewedUiState: RecentMovieViewedUiState) : SearchUIEvent
    data class ClickActorEvent(val actorID: Int) : SearchUIEvent
    object ClickBackEvent : SearchUIEvent
    object ClickRetryEvent : SearchUIEvent
    object ClickVoiceEvent : SearchUIEvent
}