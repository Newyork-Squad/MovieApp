package com.karrar.movieapp.ui.explore.exploreUIState

sealed interface ExploringUIEvent {
    object SearchEvent : ExploringUIEvent
    data class OpenDetails(val mediaId: Int, val isTv: Boolean) : ExploringUIEvent
}