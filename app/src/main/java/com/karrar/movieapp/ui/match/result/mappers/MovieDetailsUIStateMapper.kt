package com.karrar.movieapp.ui.match.result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.MovieDetails
import com.karrar.movieapp.ui.match.MatchUiState
import javax.inject.Inject

data class MovieDuration(val hours: Int, val minutes: Int)

class MovieDetailsUIStateMapper @Inject constructor() : Mapper<MovieDetails, MatchUiState.MovieDetailsUIState> {
    override fun map(input: MovieDetails): MatchUiState.MovieDetailsUIState {
        val duration = formatMovieDuration(input.movieDuration)
        return MatchUiState.MovieDetailsUIState(
            id = input.movieId,
            image = input.movieImage,
            name = input.movieName,
            releaseDate = input.movieReleaseDate,
            genres = input.movieGenres,
            hours = duration.hours,
            minutes = duration.minutes,
            specialNumber = input.movieDuration,
            review = input.movieReview,
            voteAverage = input.movieVoteAverage,
            overview = input.movieOverview,
        )
    }

    private fun formatMovieDuration(duration: Int): MovieDuration {
        return MovieDuration(hours = duration.div(60), minutes = duration.rem(60))
    }

}

