package com.karrar.movieapp.domain.usecases.search

import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.domain.mappers.search.SearchHistorySuggestMapper
import com.karrar.movieapp.domain.mappers.search.SearchKeywordMapper
import com.karrar.movieapp.domain.models.SearchKeyword
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSearchForKeywordUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val searchKeywordMapper: SearchKeywordMapper,
    private val searchHistorySuggestMapper: SearchHistorySuggestMapper,

    ) {
    suspend operator fun invoke(query: String, page: Int = 1): List<SearchKeyword> {

        val searchQuery = query.trim()
        if (searchQuery.isEmpty()) return emptyList()

        val searchKeywordDto = movieRepository.getSearchKeywords(query, page) ?: emptyList()
        val searchKeywords = searchKeywordDto.map { dto -> searchKeywordMapper.map(dto) }

        val historyEntities = movieRepository.getAllSearchHistory().first()

        val historyKeywordsAll = historyEntities
            .map { searchHistorySuggestMapper.map(it) }

        val startsWithMatches = historyKeywordsAll.filter {
            it.keyword.startsWith(searchQuery, ignoreCase = true)
        }

        val containsMatches = historyKeywordsAll.filter {
            !it.keyword.startsWith(searchQuery, ignoreCase = true) &&
                    it.keyword.contains(searchQuery, ignoreCase = true)
        }

        val localFiltered = (startsWithMatches + containsMatches)
            .distinctBy { it.keyword.lowercase().trim() }


        val combinedMap = linkedMapOf<String, SearchKeyword>()

        for (searchHistory in localFiltered) {
            combinedMap[searchHistory.keyword] = searchHistory
        }

        for (searchKeyword in searchKeywords) {
            if (!combinedMap.containsKey(searchKeyword.keyword)) {
                combinedMap[searchKeyword.keyword] = searchKeyword
            }
        }

        return combinedMap.values.toList()
    }
}