package com.karrar.movieapp.ui.match_result

sealed interface MatchResultUiEvent {

    object NavigateBack: MatchResultUiEvent
    object ViewMovieDetails : MatchResultUiEvent
    data class PlayYoutubeTrailer(val movieId : Int) : MatchResultUiEvent
    data class SaveMovie(val movieId : Int) : MatchResultUiEvent
}