package com.karrar.movieapp.ui.search.uiStatMapper

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.SearchKeyword
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchKeywordUIState
import javax.inject.Inject

class SearchKeywordUiStateMapper @Inject constructor(): Mapper<SearchKeyword, SearchKeywordUIState> {
    override fun map(input: SearchKeyword): SearchKeywordUIState {
        return SearchKeywordUIState(
            input.keyword,
            input.isFromHistory
        )
    }
}