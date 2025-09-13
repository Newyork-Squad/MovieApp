package com.karrar.movieapp.domain.usecases.home.getData

import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.data.repository.SeriesRepository
import com.karrar.movieapp.domain.mappers.movie.RecentMovieViewedMapper
import com.karrar.movieapp.domain.mappers.series.RecentSeriesViewedMapper
import com.karrar.movieapp.domain.models.Media
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRecentViewedUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val movieViewedMapper: RecentMovieViewedMapper,
    private val seriesRepository: SeriesRepository,
    private val seriesViewedMapper: RecentSeriesViewedMapper,
) {

    suspend  operator fun invoke(): Flow<List<Media>> {


        val recentMoviesFlow = movieRepository.getRecentMovieViewed().map { it.map(movieViewedMapper::map) }
        val recentSeriesFlow = seriesRepository.getRecentSeriesViewed().map { it.map(seriesViewedMapper::map) }

        return combine(recentMoviesFlow, recentSeriesFlow) { movies, series ->
            movies + series
        }
    }
}