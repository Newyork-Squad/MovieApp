package com.karrar.movieapp.ui.profile.watchhistory

data class WatchHistoryUiState(
    val allMedia: List<MediaHistoryUiState> = emptyList(),
    val error: List<Error> = emptyList(),
    val isVisible:Boolean=true
)

data class Error(
    val code: Int,
    val message: String,
)