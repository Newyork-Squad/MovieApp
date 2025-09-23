package com.karrar.movieapp.domain.usecases.search

import com.karrar.movieapp.data.local.database.entity.SearchHistoryEntity
import com.karrar.movieapp.data.repository.MovieRepository
import javax.inject.Inject

class DeleteSearchHistoryItemUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(item: SearchHistoryEntity){
        return movieRepository.deleteSearchItem(item)
    }
}