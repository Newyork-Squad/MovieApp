package com.karrar.movieapp.domain.usecases.search

import com.karrar.movieapp.data.repository.MovieRepository
import javax.inject.Inject

class ClearAllSearchHistoryUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke() {
        repository.clearAllSearchHistory()
    }
}
