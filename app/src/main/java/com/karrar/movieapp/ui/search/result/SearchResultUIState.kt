package com.karrar.movieapp.ui.search.result

import androidx.paging.PagingData
import com.karrar.movieapp.ui.allMedia.Error
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaTypes
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaUIState
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchKeywordUIState
import kotlinx.coroutines.flow.Flow

data class SearchResultUIState(
    val searchInput: String = "",
    val searchTypes: MediaTypes = MediaTypes.MOVIE,
    val searchResult: PagingData<MediaUIState>? = null,
    val searchKeywordResult: Flow<PagingData<SearchKeywordUIState>>? = null,
    val loading: Boolean = false,
    val error: List<Error> = emptyList(),
    val isEmpty: Boolean = false
)
