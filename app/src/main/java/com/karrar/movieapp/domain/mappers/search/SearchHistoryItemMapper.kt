package com.karrar.movieapp.domain.mappers.search

import com.karrar.movieapp.data.local.database.entity.SearchHistoryEntity
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchHistoryUIState
import javax.inject.Inject

class SearchHistoryItemMapper @Inject constructor() :
    Mapper<SearchHistoryEntity, SearchHistoryUIState> {
    override fun map(input: SearchHistoryEntity): SearchHistoryUIState {
        return SearchHistoryUIState(
            id = input.id,
            name = input.search
        )
    }

    fun map(input: SearchHistoryUIState): SearchHistoryEntity {
        return SearchHistoryEntity(
            id = input.id,
            search = input.name
        )
    }
}