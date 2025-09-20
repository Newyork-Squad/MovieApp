package com.karrar.movieapp.domain.usecases

import com.karrar.movieapp.data.local.AppConfiguration
import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.data.repository.SeriesRepository
import javax.inject.Inject


class ClearAppCacheUseCase @Inject constructor (
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
    private val appConfiguration: AppConfiguration

) {
    suspend operator fun invoke(language: String) {
        movieRepository.clearCache()
        seriesRepository.clearCache()
        appConfiguration.clearRequestDates()
    }
}
