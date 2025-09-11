package com.karrar.movieapp.domain.usecases.match

import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.domain.enums.Era
import com.karrar.movieapp.domain.enums.MatchingGenre
import com.karrar.movieapp.domain.enums.Mood
import com.karrar.movieapp.domain.enums.Runtime
import com.karrar.movieapp.domain.mappers.movie.MovieMapper
import com.karrar.movieapp.domain.models.Media
import javax.inject.Inject

class GetMatchingMoviesUseCase @Inject constructor(
    private val moviesRepository: MovieRepository,
    private val movieMapper: MovieMapper
) {
    suspend operator fun invoke(
        mood: Mood,
        genres: List<MatchingGenre>,
        runtime: Runtime,
        era: Era
    ): List<Media> {
        val response = moviesRepository.getMatchingMovies(mood, genres, runtime, era)
        return response?.map{ it -> movieMapper.map(it) } ?: throw Throwable("No Available Matches")


    }
}