package com.karrar.movieapp.domain.mappers.search

import com.karrar.movieapp.data.local.database.entity.SearchHistoryEntity
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.SearchKeyword
import javax.inject.Inject

class SearchHistorySuggestMapper @Inject constructor() :
    Mapper<SearchHistoryEntity, SearchKeyword> {
    override fun map(input: SearchHistoryEntity): SearchKeyword {
        return SearchKeyword(
            keyword = input.search,
            isFromHistory = true,
        )
    }
}