package com.karrar.movieapp.domain.usecases.movieDetails

import com.karrar.movieapp.data.repository.MovieRepository
import javax.inject.Inject

class DeleteMovieRatingUseCase@Inject constructor(
    private val movieRepository: MovieRepository,

    ) {
    suspend operator fun invoke(movieId: Int) =
        movieRepository.deleteRating(movieId)
    }