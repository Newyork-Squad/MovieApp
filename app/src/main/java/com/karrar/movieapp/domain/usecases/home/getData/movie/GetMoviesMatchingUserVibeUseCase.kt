package com.karrar.movieapp.domain.usecases.home.getData.movie

import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.domain.mappers.movie.UserMatchingMovieMapper
import com.karrar.movieapp.domain.models.Media
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMoviesMatchingUserVibeUseCase @Inject constructor(
    private val movieMapper: UserMatchingMovieMapper,
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(): Flow<List<Media>> {
        return movieRepository.getUserMatchingMovies()
            .map { it.map(movieMapper::map) }
    }
}