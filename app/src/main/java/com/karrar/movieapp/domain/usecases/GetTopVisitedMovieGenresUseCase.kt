package com.karrar.movieapp.domain.usecases

import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.domain.models.Genre
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopVisitedMovieGenresUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(): Flow<List<Genre>> {
        return movieRepository.getTopVisitedMovieGenres()
    }
}