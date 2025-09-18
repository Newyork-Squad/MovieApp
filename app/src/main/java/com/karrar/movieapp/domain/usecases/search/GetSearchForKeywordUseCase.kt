package com.karrar.movieapp.domain.usecases.search

import androidx.paging.PagingData
import androidx.paging.map
import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.domain.mappers.search.SearchKeywordMapper
import com.karrar.movieapp.domain.models.SearchKeyword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSearchForKeywordUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val mapper: SearchKeywordMapper,
) {
    suspend operator fun invoke(query: String): Flow<PagingData<SearchKeyword>> {
        return movieRepository.searchKeywordsPager(query)
            .flow
            .map { pagingData -> pagingData.map { mapper.map(it) } }
    }
}