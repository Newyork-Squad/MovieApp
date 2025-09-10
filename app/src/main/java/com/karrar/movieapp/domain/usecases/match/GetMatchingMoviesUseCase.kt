package com.karrar.movieapp.domain.usecases.match

import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.domain.mappers.ListMapper
import com.karrar.movieapp.domain.mappers.MovieMappersContainer
import com.karrar.movieapp.domain.mappers.movie.MovieMapper
import com.karrar.movieapp.domain.models.Media
import javax.inject.Inject

class GetMatchingMoviesUseCase @Inject constructor(
    private val moviesRepository: MovieRepository,
    private val movieMapper: MovieMapper
) {
    suspend operator fun invoke(
        genreIds: String,
        minRuntime: Int? = null,
        maxRuntime: Int? = null,
        earliestDate: String? = null,
        latestDate: String? = null,
        moodId: String? = null
    ): List<Media> {
        val response = moviesRepository.getMatchingMovies(
            genreIds = genreIds,
            minRuntime,
            maxRuntime,
            earliestDate = earliestDate,
            latestDate = latestDate,
            moodId = moodId
        )
        return response?.map{ it -> movieMapper.map(it) } ?: throw Throwable("No Available Matches")


    }
}