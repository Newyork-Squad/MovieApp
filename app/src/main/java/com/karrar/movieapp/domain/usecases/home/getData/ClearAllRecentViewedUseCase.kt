package com.karrar.movieapp.domain.usecases.home.getData

import com.karrar.movieapp.data.repository.MovieRepository
import javax.inject.Inject

class ClearAllRecentViewedUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke() {
        movieRepository.clearWatchHistory()
    }
}