package com.karrar.movieapp.ui.search.mediaSearchUIState

sealed class  SearchItemUiState (val priority: Int) {
    class SuggestedSearch(val data: List<SearchKeywordUIState>) : SearchItemUiState(0)
    class SearchItemHistory(val data: List<SearchHistoryUIState>) : SearchItemUiState(1)
    class RecentViewed(val data: List<RecentMovieViewedUiState>) : SearchItemUiState(2)
}