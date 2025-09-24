package com.karrar.movieapp.ui.match.questions

import com.karrar.movieapp.ui.movieDetails.movieDetailsUIState.DetailItemUIState

data class MatchQuestionUiState(
    val currentQuestionType: MatchQuestionType = MatchQuestionType.MOOD,
    val isLastQuestion: Boolean = false,
    val moodSelected: List<Choice> = emptyList(),
    val genreSelected: List<Choice> = emptyList(),
    val mediaRuntimeSelected: List<Choice> = emptyList(),
    val timePeriodSelected: List<Choice> = emptyList(),
    val isLoading: Boolean = false,
    val progress: Int = 25,
    val movies: List<MovieUIState> = emptyList(),
    val selectedMovieIndex: Int = 0,
    val error: List<ErrorUIState> = emptyList()
) {

    data class MovieUIState(
        val movieDetailsResult: MovieDetailsUIState = MovieDetailsUIState(),
        val movieCastResult: List<ActorUiState> = emptyList(),
        val movieCrewResult: List<CrewUIState> = emptyList(),
        val similarMoviesResult: List<MediaUiState> = emptyList(),
        val movieReview: List<ReviewUIState> = emptyList(),
        val detailItemResult: List<DetailItemUIState> = mutableListOf(),
        val ratingValue: Float = 0F,
    )

    data class MovieDetailsUIState(
        val id: Int = 0,
        val image: String = "",
        val name: String = "",
        val releaseDate: String = "",
        val genres: String = "",
        val review: Int = 0,
        val specialNumber: Int = 0,
        val hours: Int = 0,
        val minutes: Int = 0,
        val voteAverage: String = "",
        val overview: String = "",
    )

    data class ActorUiState(
        val id: Int = 0,
        val name: String = "",
        val imageUrl: String = "",
        val characterName: String = "",
    )

    data class CrewUIState(
        val name: String = "",
        val job: String = "",
    )

    data class MediaUiState(
        val id: Int = 0,
        val imageUrl: String = "",
        val mediaTitle: String = "",
        val mediaRate: Float = 0f,
        val mediaImage: String = ""
    )

    data class ReviewUIState(
        val content: String = "",
        val createDate: String = "",
        val userImage: String = "",
        val name: String = "",
        val userName: String = "",
        val rating: Float = 0f
    )

    data class ErrorUIState(
        val code: Int = 0,
        val message: String = "",
    )
}
