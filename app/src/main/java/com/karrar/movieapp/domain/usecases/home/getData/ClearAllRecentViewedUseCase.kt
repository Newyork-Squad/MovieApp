package com.karrar.movieapp.domain.usecases.home.getData

import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.data.repository.SeriesRepository
import javax.inject.Inject

class ClearAllRecentViewedUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
) {

    suspend operator fun invoke() {
        movieRepository.clearRecentMovieViewed()
        seriesRepository.clearRecentSeriesViewed()
    }
}