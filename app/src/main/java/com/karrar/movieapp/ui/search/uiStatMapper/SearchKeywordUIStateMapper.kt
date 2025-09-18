package com.karrar.movieapp.ui.search.uiStatMapper

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.SearchKeyword
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchKeywordUIState
import javax.inject.Inject

class SearchKeywordUIStateMapper @Inject constructor() :
    Mapper<SearchKeyword, SearchKeywordUIState> {

    override fun map(input: SearchKeyword): SearchKeywordUIState {
        return SearchKeywordUIState(
            keyword = input.keyword,
            isFromHistory = input.isFromHistory,
        )
    }
}