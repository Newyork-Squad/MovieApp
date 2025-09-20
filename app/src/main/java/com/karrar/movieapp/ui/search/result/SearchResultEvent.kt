package com.karrar.movieapp.ui.search.result

import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaUIState

sealed class SearchResultEvent {
    data class ClickMedia(val media: MediaUIState) : SearchResultEvent()
    data class ClickActor(val actorId: Int) : SearchResultEvent()
    object ClickBack : SearchResultEvent()
}
