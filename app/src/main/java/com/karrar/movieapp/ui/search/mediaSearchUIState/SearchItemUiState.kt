package com.karrar.movieapp.ui.search.mediaSearchUIState

sealed class  SearchItemUiState (val priority: Int) {
    class SearchItemHistory(val data: List<SearchHistoryUIState>) : SearchItemUiState(0)
    class RecentViewed(val data: List<RecentMovieViewedUiState>) : SearchItemUiState(1)
}