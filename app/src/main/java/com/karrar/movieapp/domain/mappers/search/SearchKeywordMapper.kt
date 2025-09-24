package com.karrar.movieapp.domain.mappers.search

import com.karrar.movieapp.data.remote.response.search.SearchKeywordDto
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.SearchKeyword
import javax.inject.Inject

class SearchKeywordMapper @Inject constructor() : Mapper<SearchKeywordDto, SearchKeyword> {
    override fun map(input: SearchKeywordDto): SearchKeyword {
        return SearchKeyword(
            keyword = input.name,
            isFromHistory = false,
        )
    }
}