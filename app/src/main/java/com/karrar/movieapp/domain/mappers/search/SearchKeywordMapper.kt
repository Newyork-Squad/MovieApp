package com.karrar.movieapp.domain.mappers.search

import com.karrar.movieapp.data.remote.response.search.SearchKeywordDto
import com.karrar.movieapp.domain.models.SearchKeyword
import javax.inject.Inject

class SearchKeywordMapper @Inject constructor() {
    fun map(input: SearchKeywordDto): SearchKeyword {
        return SearchKeyword(
            keyword = input.name,
            isFromHistory = false,
        )
    }
}