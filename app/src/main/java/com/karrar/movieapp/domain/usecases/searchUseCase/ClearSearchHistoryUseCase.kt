package com.karrar.movieapp.domain.usecases.searchUseCase

import android.util.Log
import com.karrar.movieapp.data.repository.MovieRepository
import javax.inject.Inject

class ClearSearchHistoryUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke() {
        movieRepository.clearSearchHistory()
    }
}
