package com.karrar.movieapp.ui.search.mediaSearchUIState

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

sealed class SearchItemUiState(val priority: Int) {
    class SearchItemHistory(val data: List<SearchHistoryUIState>) : SearchItemUiState(0)
    class RecentViewed(val data: List<RecentMovieViewedUiState>) : SearchItemUiState(1)

    class SuggestionsItems(
        val data: Flow<PagingData<SearchKeywordUIState>>,
        priority: Int = 0
    ) : SearchItemUiState(priority)
}
